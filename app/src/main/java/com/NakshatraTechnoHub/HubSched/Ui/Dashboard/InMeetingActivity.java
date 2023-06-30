package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.QRCodeGeneratorUtil;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

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

public class InMeetingActivity extends BaseActivity {

    MaterialCardView showQrBtn, discussionBtn, memberBtn, pantryBtn;

    MaterialButton hideQrBtn;
    ArrayList<Integer> list = new ArrayList<Integer>();

    TextView timeLeft, subjectView;
    String meetId, companyId, subject, startTime, endTime, response;

    //______________________
    ImageView qrCodeView;
    RelativeLayout inMeetingLayout, qrCodeLayout , actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);

        

        timeLeft = findViewById(R.id.time_left);
        subjectView = findViewById(R.id.subject);
        qrCodeView = findViewById(R.id.qr_code_image);

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

        startCountdownTimer(endTime, timeLeft);

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







    private void startCountdownTimer(String endTime, final TextView timeLeftTextView) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date endDate = format.parse(endTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(System.currentTimeMillis());

            // Set the date part of the current time to match the end time
            currentCalendar.set(Calendar.YEAR, endCalendar.get(Calendar.YEAR));
            currentCalendar.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
            currentCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH));

            if (currentCalendar.after(endCalendar)) {
                timeLeftTextView.setText("End time has already passed.");
                return;
            }

            long endTimeMillis = endCalendar.getTimeInMillis();
            long currentTimeMillis = currentCalendar.getTimeInMillis();

            long remainingTimeMillis = endTimeMillis - currentTimeMillis;

            CountDownTimer countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = (seconds / 60) % 60;
                    long hours = (seconds / 3600) % 24;

                    String remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                            hours, minutes, seconds);

                    if (minutes >= 60) {
                        long totalMinutes = minutes + (hours * 60);
                        remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                                totalMinutes / 60, totalMinutes % 60, seconds);
                    }

                    if (seconds >= 60) {
                        long totalSeconds = seconds + (minutes * 60) + (hours * 3600);
                        remainingTime = String.format(Locale.getDefault(), "%02d h : %02d m : %02d sec",
                                totalSeconds / 3600, (totalSeconds / 60) % 60, totalSeconds % 60);
                    }

                    timeLeftTextView.setText(remainingTime);
                }

                @Override
                public void onFinish() {
                    // Countdown timer finished, handle any necessary actions
                    timeLeftTextView.setText("Time's up!");
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





}