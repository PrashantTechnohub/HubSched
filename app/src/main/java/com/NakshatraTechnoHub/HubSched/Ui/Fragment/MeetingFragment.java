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
import androidx.core.content.ContextCompat;
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
        toolbar.setTitle("Meeting History");

        bind.refresh.setOnRefreshListener(this::getMeetingList);

        bind.pd.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMeetingList();
            }
        },400);



        return bind.getRoot();

    }

    private void getMeetingList() {
        if (isAdded()) {
            // Fragment is added to the activity
            // Access the context or perform other operations that require the fragment to be attached
            Context context = requireContext();
            // Rest of your code here

            new Receiver(context, new Receiver.ListListener() {
                @Override
                public void onResponse(JSONArray response) {
                    list.clear();
                    if (bind.refresh.isRefreshing()){
                        bind.refresh.setRefreshing(false);
                    }
                    if (response!=null && response.length() > 0){
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);

                                // Check if the meeting time has expired
                                if (hasMeetingExpired(model.getDate(), model.getEndTime())) {
                                    list.add(model);

                                }else{
                                    bind.meetingRecyclerview.setVisibility(View.GONE);
                                    bind.pd.setVisibility(View.GONE);
                                    bind.noResult.setVisibility(View.VISIBLE);
                                }

                                // Rest of your code
                            } catch (JSONException e) {
                                bind.pd.setVisibility(View.GONE);
                                ErrorHandler.handleException(requireContext(), e);
                            }
                        }


                    }else {
                        list.clear();
                        bind.meetingRecyclerview.setVisibility(View.GONE);
                        bind.pd.setVisibility(View.GONE);
                        bind.noResult.setVisibility(View.VISIBLE);
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
                            TextView meetingCountView = holder.itemView.findViewById(R.id.meetingCountView);
                            LinearLayout expiredLayout = holder.itemView.findViewById(R.id.actionLlp2);
                            LinearLayout acptDenied = holder.itemView.findViewById(R.id.actionLlp);


                            TextView statusView = holder.itemView.findViewById(R.id.statusView);

                            expiredLayout.setVisibility(View.VISIBLE);
                            acptDenied.setVisibility(View.GONE);

                            String inputDate = list.get(position).getDate();
                            SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                            try {
                                Date date = inputFormat.parse(inputDate);
                                String formattedDate = outputFormat.format(date);
                                orgName.setText(list.get(position).getOrganiser_name() );
                                meetSubject.setText(list.get(position).getSubject());
                                meetDate.setText(formattedDate);
                                meetTime.setText(list.get(position).getStartTime() + " - " + list.get(position).getEndTime());
                                meetLocation.setText(list.get(position).getRoom_address() );                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            meetingCountView.setText("Meeting has expired.");
                            meetingCountView.setTextColor(ContextCompat.getColor(context, R.color.red));

                            if (list.get(position).getAcceptance_status() != null) {
                                if (list.get(position).getAcceptance_status().equals("accepted")) {
                                    statusView.setVisibility(View.VISIBLE);
                                    statusView.setText("ACCEPTED");
                                    statusView.setTextColor(ContextCompat.getColor(context, R.color.green));
                                    statusView.setBackgroundResource(R.drawable.green_accpeted_bg);

                                }
                                if (list.get(position).getAcceptance_status().equals("declined")) {
                                    statusView.setVisibility(View.VISIBLE);
                                    statusView.setText("DECLINED");
                                    statusView.setTextColor(ContextCompat.getColor(context, R.color.red));
                                    statusView.setBackgroundResource(R.drawable.red_declined_bg);

                                }

                            }




                        }
                    }, R.layout.cl_scheduled_meeting);

                    bind.meetingRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                    bind.meetingRecyclerview.setAdapter(adapter);
                    bind.pd.setVisibility(View.GONE);
                }

                @Override
                public void onError(VolleyError error) {
                    if (bind.refresh.isRefreshing()) {
                        bind.refresh.setRefreshing(false);
                    }
                    bind.pd.setVisibility(View.GONE);
                    bind.noResult.setVisibility(View.VISIBLE);
                    ErrorHandler.handleVolleyError(getActivity(), error);
                }
            }).getMeetingList();

        }



    }
    private boolean hasMeetingExpired(String date, String endTime) {
        String DATE_FORMAT = "M/d/yyyy";
        String TIME_FORMAT = "hh:mm a";

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        try {
            // Parse the meeting date
            Date meetingDate = dateFormat.parse(date);

            // Get the current date and time
            Calendar currentDateTime = Calendar.getInstance();

            // Get the meeting end time
            Date endDateTime = timeFormat.parse(endTime);

            // Set the meeting end time with the meeting date
            Calendar meetingEndTime = Calendar.getInstance();
            meetingEndTime.setTime(meetingDate);
            meetingEndTime.set(Calendar.HOUR_OF_DAY, endDateTime.getHours());
            meetingEndTime.set(Calendar.MINUTE, endDateTime.getMinutes());

            // Check if the current date and time is after the meeting end time
            return currentDateTime.after(meetingEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }




}