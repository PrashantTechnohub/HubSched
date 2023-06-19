package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Interface.ApiInterface;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentMeetingBinding;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingFragment extends Fragment implements ApiInterface {

    ScheduleMeetingAdapter adapter;
    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    FragmentMeetingBinding bind = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bind = FragmentMeetingBinding.inflate(inflater);

        

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Meeting");

        bind.refresh.setOnRefreshListener(this::getMeetingList);

        getMeetingList();
        adapter = new ScheduleMeetingAdapter(getActivity(), list);
        ScheduleMeetingAdapter.setHandler(this);

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
                        if (bind.refresh.isRefreshing()) {
                            bind.refresh.setRefreshing(false);
                            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        
                        ErrorHandler.handleException(requireContext(), e);

                    }
                }
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


    @Override
    public void clickHandler(int meetId, Boolean ready, int pos) {
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
}