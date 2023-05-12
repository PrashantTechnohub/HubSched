package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Adapters.RoomListAdapter;
import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.EmployeeListActivity;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentHomeBinding;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentMeetingBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingFragment extends Fragment {

    ScheduleMeetingAdapter adapter;
    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    FragmentMeetingBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentMeetingBinding.inflate(inflater);


        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Meeting");

        return bind.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMeetingList();

    }

    private void getMeetingList() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET,Constant.withToken(Constant.MEETING_LIST_URL, requireContext()), null, new Response.Listener<JSONArray>() {
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

                adapter = new ScheduleMeetingAdapter(getActivity(), list);
                bind.meetingRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                bind.meetingRecyclerview.setAdapter(adapter);
//                bind.meetingRecyclerview.invalidate();
//                bind.meetingRecyclerview.removeAllViews();
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