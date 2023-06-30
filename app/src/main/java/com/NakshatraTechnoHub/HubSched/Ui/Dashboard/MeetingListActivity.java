package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityMeetingListBinding;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingListActivity extends BaseActivity {

    ActivityMeetingListBinding bind;

    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMeetingListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        bind.pd.setVisibility(View.VISIBLE);

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

                if (response !=null){
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);

                            list.add(model);
                            bind.pd.setVisibility(View.GONE);

                        } catch (JSONException e) {

                            bind.refresh.setRefreshing(false);
                            bind.pd.setVisibility(View.GONE);
                            ErrorHandler.handleException(getApplicationContext(), e);

                        }
                    }

                    bind.meetingListRecyclerview.setLayoutManager(new LinearLayoutManager(MeetingListActivity.this));
                    bind.refresh.setRefreshing(false);
                    bind.pd.setVisibility(View.GONE);
                }else {
                    bind.pd.setVisibility(View.GONE);
                    bind.noResult.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onError(VolleyError error) {
                bind.pd.setVisibility(View.GONE);
                bind.noResult.setVisibility(View.VISIBLE);
                bind.refresh.setRefreshing(false);
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).getMeetingList();

    }

}