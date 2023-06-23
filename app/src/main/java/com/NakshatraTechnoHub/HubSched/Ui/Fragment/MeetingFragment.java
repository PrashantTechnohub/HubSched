package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.InMeetingActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentMeetingBinding;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MeetingFragment extends Fragment{

    MyAdapter<ScheduleMeetingModel> adapter;

    Bitmap qrCodeBitmap;

    Context context;

    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    FragmentMeetingBinding bind = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bind = FragmentMeetingBinding.inflate(inflater);

        context = requireContext();

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Meeting");

        bind.refresh.setOnRefreshListener(this::getMeetingList);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMeetingList();
            }
        },300);



        return bind.getRoot();

    }

    private void getMeetingList() {
        new Receiver(requireContext(), new Receiver.ListListener() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);

                        
                        list.add(model);
                        if (pd.isDialogShown){
                            pd.mDismiss();
                        }
                        if (bind.refresh.isRefreshing()) {
                            bind.refresh.setRefreshing(false);
                            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        
                        ErrorHandler.handleException(requireContext(), e);

                    }
                }

              adapter=  new MyAdapter<ScheduleMeetingModel>(list, new MyAdapter.OnBindInterface() {
                    @Override
                    public void onBindHolder(MyAdapter.MyHolder holder, int position) {

                        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
                        holder.itemView.startAnimation(animation);
                        TextView orgName = holder.itemView.findViewById(R.id.meet_orgName);
                        TextView meetSubject = holder.itemView.findViewById(R.id.meet_subject);
                        TextView meetTime = holder.itemView.findViewById(R.id.meet_time);
                        TextView meetDate = holder.itemView.findViewById(R.id.meet_date);
                        TextView meetLocation = holder.itemView.findViewById(R.id.meet_location);
                        MaterialButton denyBtn = holder.itemView.findViewById(R.id.meet_deny_btn);
                        MaterialButton acptBtn = holder.itemView.findViewById(R.id.meet_accept_btn);
                        MaterialButton joinBtn = holder.itemView.findViewById(R.id.meet_join_btn);
                        LinearLayout llp = holder.itemView.findViewById(R.id.actionLlp);
                        LinearLayout llp2 = holder.itemView.findViewById(R.id.actionLlp2);
                        TextView statusView = holder.itemView.findViewById(R.id.statusView);
                        TextView countdownTimerView = holder.itemView.findViewById(R.id.meetingCountView);


                        String inputDate = list.get(position).getDate();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                        try {
                            Date date = inputFormat.parse(inputDate);
                            String formattedDate = outputFormat.format(date);
                            orgName.setText(list.get(position).getOrganiser_id() + "");
                            meetSubject.setText(list.get(position).getSubject());
                            meetDate.setText(formattedDate);
                            meetTime.setText(list.get(position).getStartTime() + " - " + list.get(position).getEndTime());
                            meetLocation.setText(list.get(position).getRoomId() + "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        if (list.get(position).getAcceptance_status() != null) {
                            if (list.get(position).getAcceptance_status().equals("accepted")) {
                                llp.setVisibility(View.GONE);
                                llp2.setVisibility(View.VISIBLE);
                                statusView.setVisibility(View.VISIBLE);
                                statusView.setText("Accepted");
                                fetchMeetingTimeStatus(list.get(position).getDate() + "", list.get(position).getStartTime() + "", list.get(position).getEndTime() + "", countdownTimerView, joinBtn, context);

                            }
                            if (list.get(position).getAcceptance_status().equals("declined")) {
                                llp.setVisibility(View.GONE);
                                statusView.setVisibility(View.VISIBLE);
                                statusView.setText("Declined");

                            }
                            if (list.get(position).getAcceptance_status().equals("pending")) {
                                llp.setVisibility(View.VISIBLE);
                            }
                        }


                        acptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                acceptDenied(list.get(position).get_id(),true, position);

                            }
                        });
                        denyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                acceptDenied(list.get(position).get_id(),false, position);

                            }
                        });

                        joinBtn.setOnClickListener(new View.OnClickListener() {
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
                }, R.layout.cl_scheduled_meeting);

                bind.meetingRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                bind.meetingRecyclerview.setAdapter(adapter);
                
            }

            @Override
            public void onError(VolleyError error) {
                if (bind.refresh.isRefreshing()) {
                    bind.refresh.setRefreshing(false);
                }
                
                ErrorHandler.handleVolleyError(getActivity(), error);
            }
        }).getMeetingList();
    }

    private void acceptDenied(int meetId, Boolean ready, int pos) {
        JSONObject params = new JSONObject();

        try {
            params.put("meetId", meetId);
            params.put("ready", ready);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new Receiver(requireContext(), new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg = response.getString("message");
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    getMeetingList();
                    if (pd.isDialogShown()){
                        pd.mDismiss();
                    }
                } catch (JSONException e) {
                    ErrorHandler.handleException(getContext(), e);
                    pd.mDismiss();
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(requireActivity(), error);

            }
        }).accept_denied_meeting(params);

    }

    private Bitmap generateQrCode(String getMeetId, String companyId, String subject, String startTime, String endTime) {

        JSONObject params = new JSONObject();

        try {
            params.put("meetId",Integer.parseInt(getMeetId));

        } catch (JSONException e) {

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
                LocalPreference.store_meetId(context, getMeetId);
                context.startActivity(intent);

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 500) {

                            String errorString = new String(error.networkResponse.data);
                            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(context, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

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


}