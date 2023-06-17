package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Interface.ApiInterface;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.InMeetingActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleMeetingAdapter extends RecyclerView.Adapter<ScheduleMeetingAdapter.ViewHolder> {
    ProgressDialog pd;
    Bitmap qrCodeBitmap;
    static ApiInterface mHandler;
    Context context;
    ArrayList<ScheduleMeetingModel> list;

    public static void setHandler(ApiInterface handler) {
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

        pd = new ProgressDialog(context);
        pd.setMessage("Please Wait ...");

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

        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String meetId =String.valueOf(list.get(position).get_id());
                String companyId =String.valueOf(list.get(position).getCompany_id());
                String subject =String.valueOf(list.get(position).getSubject());
                String startTime =String.valueOf(list.get(position).getStartTime());
                String endTime =String.valueOf(list.get(position).getEndTime());
                generateQrCode(meetId, companyId, subject, startTime, endTime);
            }
        });


    }

    private Bitmap generateQrCode(String getMeetId, String companyId, String subject, String startTime, String endTime) {
        pd.show();
        JSONObject params = new JSONObject();

        try {
            params.put("meetId",Integer.parseInt(getMeetId));

        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.GENERATE_QR_CODE_URL, context), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(context, InMeetingActivity.class);
                intent.putExtra("response", response.toString());
                intent.putExtra("meetId", getMeetId);
                intent.putExtra("companyId", companyId);
                intent.putExtra("subject", subject);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                context.startActivity(intent);
                pd.dismiss();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 500) {
                            pd.dismiss();
                            String errorString = new String(error.networkResponse.data);
                            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(context, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    pd.dismiss();
                    Log.e("CreateEMP", "onErrorResponse: ", e);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        VolleySingleton.getInstance(context).addToRequestQueue(request);

        return qrCodeBitmap;
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
            joinBtn.setVisibility(View.VISIBLE); // Set join button visibility to VISIBLE

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
