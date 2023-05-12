package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityRoomListBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.NakshatraTechnoHub.HubSched.Adapters.RoomListAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomListActivity extends BaseActivity {

    private ActivityRoomListBinding bind;
    private RoomListAdapter adapter;
    ArrayList<RoomListModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRoomListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        bind.addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomListActivity.this, RoomManagementActivity.class));
            }
        });

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRoomList();
            }
        });

        bind.roomListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RoomListAdapter(this, list);

        bind.roomListRecyclerView.setAdapter(adapter);

        getRoomList();

    }

    private void getRoomList() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.MEET_ROOMS_URL,RoomListActivity.this), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("TAG", "onResponse: " +response);

                list.clear();

                if (response != null){
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject object = response.getJSONObject(i);
                            RoomListModel model = new Gson().fromJson(object.toString(),RoomListModel.class);
                            list.add(model);

                            if (bind.refresh.isRefreshing()){
                                bind.refresh.setRefreshing(false);
                                Toast.makeText(RoomListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    adapter = new RoomListAdapter(getApplicationContext(), list);
                    bind.roomListRecyclerView.setAdapter(adapter);
                }else{
                    bind.roomListRecyclerView.setVisibility(View.GONE);
                    bind.noResult.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onError: " +error.getMessage());
            }
        });

        queue.add(jsonArrayRequest);
    }
}