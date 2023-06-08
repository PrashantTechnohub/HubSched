package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Interface.MeetingInterface;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleMeetingAdapter extends RecyclerView.Adapter<ScheduleMeetingAdapter.ViewHolder> {

    static MeetingInterface mHandler;
    Context context;
    ArrayList<ScheduleMeetingModel> list;

    public static void setHandler(MeetingInterface handler) {
        mHandler = handler;
    }

    public ScheduleMeetingAdapter(Context context, ArrayList<ScheduleMeetingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ScheduleMeetingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_scheduled_meeting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleMeetingAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String inputDate = list.get(position).getDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            String formattedDate = outputFormat.format(date);
            holder.orgName.setText(list.get(position).getOrganiser_id() + "");
            holder.meetSubject.setText(list.get(position).getSubject());
            holder.meetDate.setText(formattedDate);
            holder.meetTime.setText(list.get(position).getStartTime() + " - " + list.get(position).getEndTime());
            holder.meetLocation.setText(list.get(position).getRoomId() + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (list.get(position).getAcceptance_status() != null) {
            if (list.get(position).getAcceptance_status().equals("accepted")) {
                holder.llp.setVisibility(View.GONE);
                holder.llp2.setVisibility(View.VISIBLE);
                holder.statusView.setVisibility(View.VISIBLE);
                holder.statusView.setText("Accepted");
                fetchMeetingTimeStatus(list.get(position).getDate() + "", list.get(position).getStartTime() + "", list.get(position).getEndTime() + "", holder.countdownTimerView, holder.joinBtn, context);

            }
            if (list.get(position).getAcceptance_status().equals("declined")) {
                holder.llp.setVisibility(View.GONE);
                holder.statusView.setVisibility(View.VISIBLE);
                holder.statusView.setText("Declined");

            }
            if (list.get(position).getAcceptance_status().equals("pending")) {
                holder.llp.setVisibility(View.VISIBLE);
            }
        }


        holder.acptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.clickHandler(list.get(position).get_id(), true, position);
            }
        });
        holder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.clickHandler(list.get(position).get_id(), false, position);


            }
        });


    }

    private static void fetchMeetingTimeStatus(String date, String startTime, String endTime, TextView countdownTimerView, MaterialButton joinBtn, Context context) {
        String DATE_FORMAT = "M/d/yyyy"; // Updated date format
        String TIME_FORMAT = "hh:mm a";

        // Get current date and time
        Calendar currentDateTime = Calendar.getInstance();

        // Parse meeting date
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date meetingDate = null;
        try {
            meetingDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Parse start time
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        Date startDateTime = null;
        try {
            startDateTime = timeFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Parse end time
        Date endDateTime = null;
        try {
            endDateTime = timeFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Combine meeting date with start and end times
        Calendar meetingStartTime = Calendar.getInstance();
        meetingStartTime.setTime(meetingDate);
        meetingStartTime.set(Calendar.HOUR_OF_DAY, startDateTime.getHours());
        meetingStartTime.set(Calendar.MINUTE, startDateTime.getMinutes());

        Calendar meetingEndTime = Calendar.getInstance();
        meetingEndTime.setTime(meetingDate);
        meetingEndTime.set(Calendar.HOUR_OF_DAY, endDateTime.getHours());
        meetingEndTime.set(Calendar.MINUTE, endDateTime.getMinutes());

        // Check meeting time status
        if (currentDateTime.before(meetingStartTime)) {
            // Meeting is upcoming
            long remainingTimeInMillis = meetingStartTime.getTimeInMillis() - currentDateTime.getTimeInMillis();
            long minutes = remainingTimeInMillis / (1000 * 60);
            if (minutes >= 60) {
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;
                if (hours >= 24) {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    String remainingTimeText = String.format("%02d", days) + " days " + String.format("%02d", remainingHours) + " hours " + String.format("%02d", remainingMinutes) + " min left";
                    countdownTimerView.setText(remainingTimeText);
                } else {
                    String remainingTimeText = hours + " hour " + String.format("%02d", remainingMinutes) + " min left";
                    countdownTimerView.setText(remainingTimeText);
                }
            } else {
                String remainingTimeText = minutes + " min left";
                countdownTimerView.setText(remainingTimeText);
            }
            countdownTimerView.setTextColor(Color.BLUE); // Set text color to blue for upcoming meetings
            if (minutes >= 0 && minutes <= 15) {
                joinBtn.setVisibility(View.VISIBLE); // Set join button visibility to VISIBLE
            }
        } else if (currentDateTime.after(meetingEndTime)) {
            // Meeting has expired
            countdownTimerView.setText("Meeting has expired.");
            countdownTimerView.setTextColor(context.getResources().getColor(R.color.red));
        } else if (currentDateTime.equals(meetingStartTime) || (currentDateTime.after(meetingStartTime) && currentDateTime.before(meetingEndTime))) {
            // Meeting is ongoing
            countdownTimerView.setText("Meeting is ongoing.");
            countdownTimerView.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView orgName, meetSubject, meetTime, meetDate, meetLocation, statusView, countdownTimerView;

        MaterialButton joinBtn, denyBtn, acptBtn;

        CountDownTimer countDownTimer;

        LinearLayout llp, llp2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orgName = itemView.findViewById(R.id.meet_orgName);
            meetSubject = itemView.findViewById(R.id.meet_subject);
            meetTime = itemView.findViewById(R.id.meet_time);
            meetDate = itemView.findViewById(R.id.meet_date);
            meetLocation = itemView.findViewById(R.id.meet_location);
            denyBtn = itemView.findViewById(R.id.meet_deny_btn);
            acptBtn = itemView.findViewById(R.id.meet_accept_btn);
            joinBtn = itemView.findViewById(R.id.meet_join_btn);
            llp = itemView.findViewById(R.id.actionLlp);
            llp2 = itemView.findViewById(R.id.actionLlp2);
            statusView = itemView.findViewById(R.id.statusView);
            countdownTimerView = itemView.findViewById(R.id.meetingCountView);


        }
    }
}
