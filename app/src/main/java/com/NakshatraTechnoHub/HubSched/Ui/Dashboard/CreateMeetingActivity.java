package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import static com.NakshatraTechnoHub.HubSched.Api.Constant.MEETING_LIST_URL;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.NakshatraTechnoHub.HubSched.Adapters.BookedSlotAdapter;
import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.MeetingEmpListModel;
import com.NakshatraTechnoHub.HubSched.Models.BookedSlotModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.UtilHelper.CustomSelectionSpinner;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateMeetingBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    ArrayList<MeetingEmpListModel> empList = new ArrayList<>();

    ArrayList<BookedSlotModel> bookedSlotList = new ArrayList<>();

    BookedSlotAdapter bookedSlotAdapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityCreateMeetingBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        roomName = getIntent().getStringExtra("roomName");
        roomId = getIntent().getStringExtra("roomId");
        selectedDate = getIntent().getStringExtra("selectedDate");
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait ...");
        bind.progressBar.setVisibility(View.VISIBLE);

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
                pd.show();
                setMeetingTimeApiCall();
            }
        });


        //New
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
                CustomSelectionSpinner.show(CreateMeetingActivity.this , "Done", stringArray, new SelectionCompleteListener() {
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

                        if (!empList.isEmpty()){
                            bind.selectedEmployeesView.setVisibility(View.VISIBLE);
                            bind.selectedEmployeesView.setText(empList);
                            bind.text.setText("Selected Employees ");
                        }else{
                            bind.selectedEmployeesView.setVisibility(View.GONE);
                        }

                    }
                });


            }
        });

        bind.createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String subject = bind.meetingSubject.getText().toString();
                if (subject.isEmpty()) {
                    pd.dismiss();
                    bind.meetingSubject.requestFocus();
                    bind.meetingSubject.setError("Empty");
                }  else {
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
            params.put("endTime", endTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(CreateMeetingActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.MEETS_FOR_DATE_URL, CreateMeetingActivity.this), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = response.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            BookedSlotModel model = new Gson().fromJson(jsonObject.toString(),BookedSlotModel.class);
                            bookedSlotList.add(model);
                            bind.progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }




                bookedSlotAdapter = new BookedSlotAdapter(CreateMeetingActivity.this, bookedSlotList);
                bind.bookedMeetingRecyclerview.setLayoutManager(new LinearLayoutManager(CreateMeetingActivity.this));
                bind.bookedMeetingRecyclerview.setAdapter(bookedSlotAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.networkResponse.statusCode == 500) {
                        pd.dismiss();
                        String errorString = new String(error.networkResponse.data);
                    }

                } catch (Exception e) {
                    Log.e("CreateEMP", "onErrorResponse: ", e);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);

    }

    private void setMeetingTimeApiCall() {
        JSONObject params = new JSONObject();
        try {
            params.put("date", selectedDate);
            params.put("startTime", startTime);
            params.put("roomId", roomId);
            params.put("endTime", endTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //

        RequestQueue queue = Volley.newRequestQueue(CreateMeetingActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.EMPLOYEE_LIST_FOR_MEET_URL, CreateMeetingActivity.this), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray array = response.getJSONArray("userList");
                    bind.bookedMeetingRecyclerview.setVisibility(View.GONE);
                    confirmMeeting(array);
                    pd.dismiss();
                } catch (JSONException e) {
                    pd.dismiss();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.networkResponse.statusCode == 500) {
                        String errorString = new String(error.networkResponse.data);
                        pd.dismiss();
                        Toast.makeText(CreateMeetingActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);

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
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(CreateMeetingActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CREATE_MEETING_URL, CreateMeetingActivity.this), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pd.dismiss();
                Toast.makeText(CreateMeetingActivity.this, "Meeting Created", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.networkResponse.statusCode == 500) {
                        String errorString = new String(error.networkResponse.data);
                        pd.dismiss();
                        Toast.makeText(CreateMeetingActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);
    }


}