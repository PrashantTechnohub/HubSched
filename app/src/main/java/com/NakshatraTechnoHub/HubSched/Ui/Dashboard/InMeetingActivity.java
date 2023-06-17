package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.QRCodeGeneratorUtil;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InMeetingActivity extends AppCompatActivity {

    MaterialCardView showQrBtn, discussionBtn, memberBtn, pantryBtn;

    MaterialButton hideQrBtn;

    TextView timeLeft, subjectView;
    String meetId, companyId, subject, startTime, endTime, response;

    //__________________________________
    ImageView qrCodeView;
    RelativeLayout inMeetingLayout, qrCodeLayout , actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_meeting);

        pd.mShow(this);

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
        meetId = intent.getStringExtra("meetId");
        companyId = intent.getStringExtra("companyId");
        subject = intent.getStringExtra("subject");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        response = getIntent().getStringExtra("response");


        subjectView.setText(subject);


        generateQrCode();

        startCountdownTimer(endTime, timeLeft);

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

        pantryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InMeetingActivity.this, PantryOrderedListActivity.class);
                intent.putExtra("meetId", meetId);
                intent.putExtra("companyId", companyId);

                startActivity(intent);
            }
        });
        discussionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InMeetingActivity.this, ChatActivity.class);
                intent.putExtra("meetId", meetId);
                intent.putExtra("companyId", companyId);

                startActivity(intent);
            }
        });
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