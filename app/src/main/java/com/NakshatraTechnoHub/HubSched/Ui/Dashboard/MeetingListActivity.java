package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

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

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        ScheduleMeetingModel model = new Gson().fromJson(object.toString(),ScheduleMeetingModel.class);

                        list.add(model);

                    } catch (JSONException e) {

                        bind.refresh.setRefreshing(false);
                        ErrorHandler.handleException(getApplicationContext(), e);

                    }
                }

                bind.meetingListRecyclerview.setLayoutManager(new LinearLayoutManager(MeetingListActivity.this));
                bind.refresh.setRefreshing(false);

            }

            @Override
            public void onError(VolleyError error) {
                bind.refresh.setRefreshing(false);
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).getMeetingList();

    }

}