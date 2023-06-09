package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import static com.NakshatraTechnoHub.HubSched.Api.Constant.MEET_REQUEST_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Interface.MeetingInterface;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.ScannerDeviceDashboard.ScannerDeviceActivity;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentMeetingBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingFragment extends Fragment implements MeetingInterface {

    ScheduleMeetingAdapter adapter;
    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    FragmentMeetingBinding bind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentMeetingBinding.inflate(inflater);


        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Meeting");

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeetingList();
            }
        });

        getMeetingList();
        adapter = new ScheduleMeetingAdapter(getActivity(), list);
        ScheduleMeetingAdapter.setHandler(this);

        return bind.getRoot();

    }

    private void getMeetingList() {


        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.MEETING_LIST_URL, requireContext()), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);

                        list.add(model);
                        if (bind.refresh.isRefreshing()){
                            bind.refresh.setRefreshing(false);
                            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                bind.meetingRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                bind.meetingRecyclerview.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (bind.refresh.isRefreshing()){
                    bind.refresh.setRefreshing(false);
                }
                Log.d("Room List Error", "onErrorResponse: " + error);
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(arrayRequest);


    }


    @Override
    public void clickHandler(int meetId, Boolean ready, int pos) {
        JSONObject params = new JSONObject();

        try {
            params.put("meetId", meetId);
            params.put("ready", ready);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(MEET_REQUEST_URL, requireContext()), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String msg = response.getString("message");
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });


        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }
}