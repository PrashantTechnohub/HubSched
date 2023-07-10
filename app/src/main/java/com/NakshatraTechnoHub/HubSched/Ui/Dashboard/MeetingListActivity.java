package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityMeetingListBinding;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MeetingListActivity extends BaseActivity {

    ActivityMeetingListBinding bind;

    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    String start;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMeetingListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);


        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bind.addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeetingListActivity.this, RoomListActivity.class));
            }
        });

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bind.pd.setVisibility(View.GONE);
                getMeetingList();
            }
        });

        getMeetingList();
    }

    private void getMeetingList() {

        new Receiver(MeetingListActivity.this, new Receiver.ListListener() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                if (bind.refresh.isRefreshing()){
                    bind.refresh.setRefreshing(false);
                }
                if (response != null && response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);

                            ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);
                            if(model.getOrganiser_id() != Integer.parseInt(LocalPreference.get_Id(MeetingListActivity.this)))
                                continue;
                            list.add(model);

                        } catch (JSONException e) {
                            bind.pd.setVisibility(View.GONE);
                            ErrorHandler.handleException(getApplicationContext(), e);

                        }
                    }


                    adapter = new MyAdapter<ScheduleMeetingModel>(list, new MyAdapter.OnBindInterface() {
                        @Override
                        public void onBindHolder(MyAdapter.MyHolder holder, int position) {

                            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
                            holder.itemView.startAnimation(animation);
                            TextView orgName = holder.itemView.findViewById(R.id.meet_orgName);
                            TextView meetSubject = holder.itemView.findViewById(R.id.meet_subject);
                            TextView meetTime = holder.itemView.findViewById(R.id.meet_time);
                            TextView meetDate = holder.itemView.findViewById(R.id.meet_date);
                            TextView meetLocation = holder.itemView.findViewById(R.id.meet_location);
                            MaterialButton delete = holder.itemView.findViewById(R.id.meet_delete_btn);
                            MaterialButton cancel = holder.itemView.findViewById(R.id.meet_cancel_btn);

                            LinearLayout llp = holder.itemView.findViewById(R.id.actionLlp);
                            LinearLayout llp2 = holder.itemView.findViewById(R.id.actionLlp2);
                            LinearLayout llp3 = holder.itemView.findViewById(R.id.actionLlp3);

                            llp.setVisibility(View.GONE);
                            llp2.setVisibility(View.GONE);
                            llp3.setVisibility(View.VISIBLE);


                            String inputDate = list.get(position).getDate();
                            SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                            try {
                                Date date = inputFormat.parse(inputDate);
                                String formattedDate = outputFormat.format(date);
                                orgName.setText(list.get(position).getOrganiser_name());
                                meetSubject.setText(list.get(position).getSubject());
                                meetDate.setText(formattedDate);
                                meetTime.setText(list.get(position).getStartTime() + " - " + list.get(position).getEndTime());
                                meetLocation.setText(list.get(position).getRoom_address() );
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteMeeting(list.get(position).get_id());
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cancelMeeting(list.get(position).get_id());
                                }
                            });




                        }
                    }, R.layout.cl_scheduled_meeting);

                    bind.meetingListRecyclerview.setLayoutManager(new LinearLayoutManager(MeetingListActivity.this));
                    bind.meetingListRecyclerview.setAdapter(adapter);


                }else {
                    bind.meetingListRecyclerview.setVisibility(View.GONE);
                    bind.pd.setVisibility(View.GONE);
                    bind.noResult.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onError(VolleyError error) {

                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).getMeetingList();

    }

    private void cancelMeeting(int id) {

        new Receiver(MeetingListActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {

                try {
                    String message = object.getString("message");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        }).cancel_meeting(id);


    }

    private void deleteMeeting(int id) {



        new Receiver(MeetingListActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        }).delete_meeting(id);


    }

}