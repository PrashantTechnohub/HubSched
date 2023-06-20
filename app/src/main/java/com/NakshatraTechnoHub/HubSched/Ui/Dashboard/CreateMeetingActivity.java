package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.NakshatraTechnoHub.HubSched.Models.BookedSlotModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.CustomSelectionSpinner;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateMeetingBinding;
import com.android.volley.VolleyError;
import com.devstune.searchablemultiselectspinner.SearchableItem;
import com.devstune.searchablemultiselectspinner.SelectionCompleteListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateMeetingActivity extends BaseActivity {
    ActivityCreateMeetingBinding bind;
    Calendar c = Calendar.getInstance();
    String roomName, roomId, selectedDate, startTime, endTime;
    JSONArray EmpIdList;


    ArrayList<BookedSlotModel> bookedSlotList = new ArrayList<>();

    MyAdapter<BookedSlotModel> bookedSlotAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityCreateMeetingBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);


        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        roomId = intent.getStringExtra("roomId");
        selectedDate = intent.getStringExtra("selectedDate");


        bind.actionBar.setText("Booked Slots");
        getBookedSlotApiCall();

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bind.selectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // date picker dialog
                new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                        int adjustedHour = selectedHour % 12;
                        if (adjustedHour == 0) {
                            adjustedHour = 12;
                        }

                        // Format the selected time to 12-hour format
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        c.set(Calendar.HOUR_OF_DAY, selectedHour);
                        c.set(Calendar.MINUTE, selectedMinute);
                        startTime = timeFormat.format(c.getTime());

                        // Display the selected time
                        bind.toView.setText(startTime);
                    }


                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();


            }
        });
        bind.selectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // date picker dialog
                new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                        Calendar currentTime = Calendar.getInstance(); // Get the current time

                        // Create a calendar instance for the selected time
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedTime.set(Calendar.MINUTE, selectedMinute);

                        // Check if the selected time is in the past
                        if (selectedTime.before(currentTime)) {
                            // Display a message to the user or perform any desired action
                            Toast.makeText(CreateMeetingActivity.this, "Invalid time selection", Toast.LENGTH_SHORT).show();
                            return; // Exit the method
                        }

                        int adjustedHour = selectedHour % 12;
                        if (adjustedHour == 0) {
                            adjustedHour = 12;
                        }

                        // Format the selected time to 12-hour format
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        c.set(Calendar.HOUR_OF_DAY, selectedHour);
                        c.set(Calendar.MINUTE, selectedMinute);
                        endTime = timeFormat.format(c.getTime());

                        // Display the selected time
                        bind.fromView.setText(endTime);
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();


            }
        });
        bind.checkAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMeetingTimeApiCall();
            }
        });

    }

    private ArrayList<SearchableItem> getStringArray(JSONArray empList) {
        ArrayList<SearchableItem> stringArray = new ArrayList<>();

        try {
            for (int i = 0; i < empList.length(); i++) {
                JSONObject empObject = empList.getJSONObject(i);
                SearchableItem item = new SearchableItem(empObject.getString("email"), empObject.getInt("_id") + "");
                stringArray.add(item);
            }
        } catch (Exception e) {
            ErrorHandler.handleException(getApplicationContext(), e);

        }


        return stringArray;
    }

    public static String formatIdList(List<String> idList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            stringBuilder.append((i + 1) + ". " + idList.get(i));
            if (i < idList.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private void confirmMeeting(JSONArray empList) {
        bind.createMeetLayout.setVisibility(View.VISIBLE);
        bind.ll1.setVisibility(View.GONE);
        bind.roomName.setText(roomName);

        bind.actionBar.setText("Create Meeting");
        ArrayList<SearchableItem> stringArray = getStringArray(empList);

        // Handle item selection
        bind.roomName.setEnabled(false); // Disabling the TextInputEditText

        bind.selectEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomSelectionSpinner.show(CreateMeetingActivity.this, "Done", stringArray, new SelectionCompleteListener() {
                    @Override
                    public void onCompleteSelection(@NonNull ArrayList<SearchableItem> arrayList) {
                        EmpIdList = new JSONArray();
                        for (SearchableItem item : arrayList) {
                            EmpIdList.put(Integer.parseInt(item.getCode()));
                        }

                        List<String> mailList = new ArrayList<>();
                        for (SearchableItem item : arrayList) {
                            mailList.add(item.getText());
                        }

                        String empList = formatIdList(mailList);

                        if (!empList.isEmpty()) {
                            bind.selectedEmployeesView.setVisibility(View.VISIBLE);
                            bind.selectedEmployeesView.setText(empList);
                            bind.text.setText("Selected Employees ");
                        } else {
                            bind.selectedEmployeesView.setVisibility(View.GONE);
                        }

                    }
                });


            }
        });

        bind.createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = bind.meetingSubject.getText().toString();
                if (subject.isEmpty()) {
                    bind.meetingSubject.requestFocus();
                    bind.meetingSubject.setError("Empty");
                } else {
                    scheduleMeetingApiCall(subject, EmpIdList);
                }
            }
        });

    }

    private void getBookedSlotApiCall() {
        JSONObject params = new JSONObject();
        try {
            params.put("date", selectedDate);
            params.put("startTime", startTime);
            params.put("roomId", roomId);
            params.put("endTime", endTime);
        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }
        new Receiver(CreateMeetingActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        BookedSlotModel model = new Gson().fromJson(jsonObject.toString(), BookedSlotModel.class);
                        bookedSlotList.add(model);
                    }

                    // Create and set the adapter
                    bookedSlotAdapter = new MyAdapter<>(bookedSlotList, new MyAdapter.OnBindInterface() {
                        @Override
                        public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                            TextView startTime = holder.itemView.findViewById(R.id.start_time);
                            TextView endTime = holder.itemView.findViewById(R.id.end_Time);
                            startTime.setText(bookedSlotList.get(position).getStartTime());
                            endTime.setText(bookedSlotList.get(position).getEndTime());
                        }
                    }, R.layout.cl_create_meeting);
                    bind.bookedMeetingRecyclerview.setLayoutManager(new LinearLayoutManager(CreateMeetingActivity.this));
                    bind.bookedMeetingRecyclerview.setAdapter(bookedSlotAdapter);
                } catch (JSONException e) {
                    ErrorHandler.handleException(getApplicationContext(), e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);
            }
        }).getBookedSlotList(params);

    }

    private void setMeetingTimeApiCall() {


        JSONObject params = new JSONObject();
        try {
            params.put("date", selectedDate);
            params.put("startTime", startTime);
            params.put("roomId", roomId);
            params.put("endTime", endTime);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }


        new Receiver(CreateMeetingActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray array = response.getJSONArray("userList");
                    bind.bookedMeetingRecyclerview.setVisibility(View.GONE);
                    confirmMeeting(array);

                } catch (JSONException e) {

                    ErrorHandler.handleException(getApplicationContext(), e);
                }

            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).emp_list_for_meeting(params);


    }

    private void scheduleMeetingApiCall(String subject, JSONArray empIdList) {


        JSONObject params = new JSONObject();
        String _id = LocalPreference.get_Id(CreateMeetingActivity.this);
        try {
            params.put("date", selectedDate);
            params.put("subject", subject);
            params.put("startTime", startTime);
            params.put("roomId", roomId);
            params.put("organiser_id", _id);
            params.put("endTime", endTime);
            params.put("employee_ids", empIdList);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }
        new Receiver(CreateMeetingActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {

                Toast.makeText(CreateMeetingActivity.this, "Meeting Created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).create_meeting(params);

    }

}