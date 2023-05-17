package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.Adapters.EmpListAdapter;
import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityEmployeeListBinding;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityMeetingListBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingListActivity extends BaseActivity {

    ActivityMeetingListBinding bind;

    ScheduleMeetingAdapter adapter;
    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMeetingListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        getMeetingList();
    }

    private void getMeetingList() {
        RequestQueue queue = Volley.newRequestQueue(MeetingListActivity.this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.MEETING_LIST_URL, MeetingListActivity.this), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        ScheduleMeetingModel model = new Gson().fromJson(object.toString(),ScheduleMeetingModel.class);

                        list.add(model);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                adapter = new ScheduleMeetingAdapter(MeetingListActivity.this, list);
                bind.meetingListRecyclerview.setLayoutManager(new LinearLayoutManager(MeetingListActivity.this));
                bind.meetingListRecyclerview.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Room List Error", "onErrorResponse: " +error);
            }
        });

        queue.add(arrayRequest);

    }

}