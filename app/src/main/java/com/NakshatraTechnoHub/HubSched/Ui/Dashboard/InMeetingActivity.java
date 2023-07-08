package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.CustomErrorDialog;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.QRCodeGeneratorUtil;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InMeetingActivity extends BaseActivity {

    MaterialCardView showQrBtn, discussionBtn, memberBtn, pantryBtn, extendMeeting;

    MaterialButton hideQrBtn;
    ArrayList<Integer> list = new ArrayList<Integer>();

    TextView timeLeft, subjectView;
    String meetId, companyId, subject, startTime, endTime, response;

    int selectedMinutes = 0;
    AlertDialog dialog;
    CountDownTimer countDownTimer;
    boolean success;
    //______________________
    ImageView qrCodeView;
    RelativeLayout inMeetingLayout, qrCodeLayout, actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);


        timeLeft = findViewById(R.id.time_left);
        subjectView = findViewById(R.id.subject);
        qrCodeView = findViewById(R.id.qr_code_image);
        extendMeeting = findViewById(R.id.extend_meeting_time_btn);

        showQrBtn = findViewById(R.id.showQrCodeBtn);
        hideQrBtn = findViewById(R.id.hideQr_btn);
        discussionBtn = findViewById(R.id.chat_discussion_btn);
        pantryBtn = findViewById(R.id.pantry_btn);
        memberBtn = findViewById(R.id.inMeeting_member_btn);

        inMeetingLayout = findViewById(R.id.InMeetingRL);
        qrCodeLayout = findViewById(R.id.qrCodeRL);
        actionBar = findViewById(R.id.rl1);


        Intent intent = getIntent();
        meetId = LocalPreference.get_meetId(this);
        companyId = intent.getStringExtra("companyId");
        subject = intent.getStringExtra("subject");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        response = getIntent().getStringExtra("response");


        subjectView.setText(subject);


        generateQrCode();

        startCountdownTimer();

        findViewById(R.id.back).setOnClickListener(view -> finish());


        showQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inMeetingLayout.setVisibility(View.GONE);
                qrCodeLayout.setVisibility(View.VISIBLE);
                actionBar.setVisibility(View.GONE);
            }
        });
        hideQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inMeetingLayout.setVisibility(View.VISIBLE);
                qrCodeLayout.setVisibility(View.GONE);
                actionBar.setVisibility(View.VISIBLE);
            }
        });

        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJoinedMember();
            }
        });

        pantryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InMeetingActivity.this, PantryOrderedListActivity.class);
                startActivity(intent);
            }
        });
        discussionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InMeetingActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        extendMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(InMeetingActivity.this, R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_meeting_extend, null);
                builder.setView(team);
                builder.setCancelable(false);


                SeekBar seekBar = team.findViewById(R.id.seek_bar_time);
                MaterialButton check = team.findViewById(R.id.check_extend);
                MaterialButton cancel = team.findViewById(R.id.cancel_extend_meeting);
                TextView extendedTime = team.findViewById(R.id.extended_meet_time);
                // Initialize selectedMinutes with a default value
                dialog = builder.create();

                extendedTime.setText("EXTEND MEETING TIME 10 MIN");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        selectedMinutes = (progress + 1) * 10; // Multiply progress by 10 to get the selected
                        extendedTime.setText("EXTEND MEETING TIME " + selectedMinutes + " MIN");

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        selectedMinutes = 10;
                        extendedTime.setText("EXTEND MEETING TIME " + 10 + " MIN");

                        // Not needed for this case, but you can implement if required
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Not needed for this case, but you can implement if required
                    }
                });

                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        extendMeetingApiCall(selectedMinutes);
                    }
                });


                dialog.show();


            }
        });
    }

    private void extendMeetingApiCall(int selectedMinutes) {
        JSONObject object = new JSONObject();
        try {
            object.put("meetId", Integer.parseInt(meetId));
            object.put("extra_minutes", selectedMinutes);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        new Receiver(InMeetingActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    // Parse the JSON response

                    // Retrieve values from the JSON object
                    success = jsonObject.getBoolean("success");

                    String message = jsonObject.getString("message");


                    if (success) {

                        String endTime1 = jsonObject.getString("endTime");
                        endTime = endTime1;
                        startCountdownTimer();
                        CustomErrorDialog.mShow(InMeetingActivity.this, message);
                        dialog.dismiss();

                    } else {
                        Toast.makeText(InMeetingActivity.this, endTime, Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {


            }
        }).extend_meeting(object);

    }

    private void getJoinedMember() {
        new Receiver(InMeetingActivity.this, new Receiver.ListListener() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        JSONArray employeeIdsArray = object.getJSONArray("employee_ids");
                        for (int j = 0; j < employeeIdsArray.length(); j++) {
                            int employeeId = employeeIdsArray.getInt(j);
                            if (!list.contains(employeeId)) {
                                list.add(employeeId);
                            }
                        }
                    } catch (JSONException e) {
                        ErrorHandler.handleException(InMeetingActivity.this, e);
                    }
                }

                // Show employee IDs in Material Dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(InMeetingActivity.this);
                builder.setTitle("Joined Members");
                builder.setItems(getEmployeeIdStrings(list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle employee ID selection if needed
                        int selectedEmployeeId = list.get(which);
                        // TODO: Perform actions with the selected employee ID
                    }
                });

                // Add close button
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(InMeetingActivity.this, error);
            }
        }).getMeetingList();
    }

    private CharSequence[] getEmployeeIdStrings(List<Integer> employeeIds) {
        CharSequence[] idStrings = new CharSequence[employeeIds.size()];
        for (int i = 0; i < employeeIds.size(); i++) {
            int employeeId = employeeIds.get(i);
            idStrings[i] = "Employee ID: " + employeeId;
        }
        return idStrings;
    }


    private void startCountdownTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy hh:mm a", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Set the time zone to Kolkata

        try {
            Date endDate = format.parse(endTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Set the time zone to Kolkata

            if (currentCalendar.after(endCalendar)) {
                timeLeft.setText("End time has already passed.");
                return;
            }

            long endTimeMillis = endCalendar.getTimeInMillis();
            long currentTimeMillis = currentCalendar.getTimeInMillis();

            long remainingTimeMillis = endTimeMillis - currentTimeMillis;
            final long seconds = remainingTimeMillis / 1000;
            final long minutes = (seconds / 60) % 60;
            final long hours = (seconds / 3600) % 24;

            String remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d s", hours, minutes, seconds);

            timeLeft.setText(remainingTime);

            countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long updatedSeconds = millisUntilFinished / 1000;
                    long updatedMinutes = (updatedSeconds / 60) % 60;
                    long updatedHours = (updatedSeconds / 3600) % 24;
                    updatedSeconds = updatedSeconds % 60;

                    String remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d s", updatedHours, updatedMinutes, updatedSeconds);

                    timeLeft.setText(remainingTime);
                }

                @Override
                public void onFinish() {
                    timeLeft.setText("Time's up!");
                }
            };

            countDownTimer.start();
        } catch (ParseException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
            // Handle any parsing errors
        }
    }

    private void generateQrCode() {

        if (this.response != null) {
            Bitmap code = QRCodeGeneratorUtil.generateQRCode(this.response);
            qrCodeView.setImageBitmap(code);
            pd.mDismiss();
        } else {
            pd.mDismiss();
            Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        if (qrCodeLayout.getVisibility() == View.VISIBLE) {
            // If layout2 is visible, go back to layout1
            qrCodeLayout.setVisibility(View.GONE);
            inMeetingLayout.setVisibility(View.VISIBLE);
            actionBar.setVisibility(View.VISIBLE);
        } else {
            // Otherwise, perform default back button functionality
            super.onBackPressed();
        }

    }
}