package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateMeetingBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateMeetingActivity extends BaseActivity {
    ActivityCreateMeetingBinding bind;

    DatePickerDialog datePickerDialog;
    final Calendar c = Calendar.getInstance();

    String toTime, fromTime, toDate, fromDate, toSelectTime, fromSelectedTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityCreateMeetingBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bind.toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // date picker dialog
                datePickerDialog = new DatePickerDialog(CreateMeetingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // set day of month , month and year value in the edit text
                        bind.toSelectedTimeLayout.setVisibility(View.VISIBLE);
                        bind.toView.setVisibility(View.GONE);

                        toDate =   dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        bind.toSelectedDateView.setText(toDate);

                       new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String AM_PM;

                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }

                                SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.getDefault());
                                toTime = dateFormat.format(new Date())+" "+hourOfDay + ":" + minute + " " +AM_PM;
                                bind.toSelectedTimeView.setText(toTime);
                            }


                        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), false).show();

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        bind.fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // date picker dialog
                datePickerDialog = new DatePickerDialog(CreateMeetingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        bind.fromSelectedTimeLayout.setVisibility(View.VISIBLE);
                        bind.fromView.setVisibility(View.GONE);

                        fromDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        bind.fromSelectedDateView.setText(fromDate);

                        new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }


                                SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.getDefault());
                                fromTime = dateFormat.format(new Date())+" "+hourOfDay + ":" + minute + " " +AM_PM;

                                bind.fromSelectedTimeView .setText(fromTime);

                            }


                        },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true).show();


                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });

        bind.checkAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bind.toSelectedTimeLayout.getVisibility() == View.GONE){
                    Toast.makeText(CreateMeetingActivity.this, "Please Select Start Time !!", Toast.LENGTH_SHORT).show();
                }else if ( bind.fromSelectedTimeLayout.getVisibility() == View.GONE){
                    Toast.makeText(CreateMeetingActivity.this, "Please Select End Time !!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CreateMeetingActivity.this, "checking", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}