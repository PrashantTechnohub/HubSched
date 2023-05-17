package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.EmpListModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateMeetingActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentHomeBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.NakshatraTechnoHub.HubSched.Adapters.RoomListAdapter;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    ScheduleMeetingAdapter adapter;
    RoomListAdapter roomListAdapter;

    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    ArrayList<RoomListModel> roomList = new ArrayList<>();
    FragmentHomeBinding bind;

    NavigationView navigationView;
    MaterialToolbar toolbar;

    RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater);

        toolbar = (MaterialToolbar) requireActivity().findViewById(R.id.topAppBar);
        navigationView = requireActivity().findViewById(R.id.navigation_view);
        String type =  LocalPreference.getType(requireContext());
        requestQueue = Volley.newRequestQueue(requireActivity());

        if (type.equals("admin")) {
            adminFunction();
        } else if (type.equals("employee")) {
            employeeFunction();
        } else if (type.equals("organiser")) {
            organizerFunction();
        }

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (type.equals("admin")) {
                    adminFunction();
                } else if (type.equals("employee")) {
                    employeeFunction();
                } else if (type.equals("organiser")) {
                    getRoomList();
                }
            }
        });



        bind.createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity() .startActivity(new Intent(requireActivity(), CreateMeetingActivity.class));
            }
        });

        bind.showCurrentMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.adminViewLayout.setVisibility(View.GONE);
                bind.userViewLayout.setVisibility(View.VISIBLE);
            }
        });

        bind.showAdminViewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.adminViewLayout.setVisibility(View.VISIBLE);
                bind.userViewLayout.setVisibility(View.GONE);
            }
        });



        ImageSlider imageSlider = bind.imageSlider;
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://img.freepik.com/free-photo/group-diverse-people-having-business-meeting_53876-25060.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://lh5.googleusercontent.com/p/AF1QipO0SKciR6pZlUgKDdQWiuLohTbmdw76jP5-dOQ_=w630-h240-k-no", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://www.incimages.com/uploaded_files/image/1920x1080/getty_473909426_129584.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://lexpeeps.in/wp-content/uploads/2020/05/1024px-Secretary_Kerry_Hosts_the_Quarterly_Millennium_Challenge_Corp_MCC_Board_of_Directors_Meeting-1024x425.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);



        return bind.getRoot();

    }


    private void adminFunction() {
        toolbar.setTitle("Admin Dashboard");
        bind.userViewLayout.setVisibility(View.GONE);
        bind.adminViewLayout.setVisibility(View.VISIBLE);

        if (bind.refresh.isRefreshing()){
            bind.refresh.setRefreshing(false);
            Toast.makeText(requireContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }

    }

    private void organizerFunction() {

        bind.imageSlider.setVisibility(View.GONE);
        toolbar.setTitle("Organizer Dashboard");
        toolbar.setNavigationIcon(null);
        bind.scheduleMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bind.showAdminViewMeeting.setVisibility(View.GONE);

        getRoomList();

    }

    private void getRoomList() {

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.MEET_ROOMS_URL, requireContext()), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        RoomListModel model = new Gson().fromJson(object.toString(),RoomListModel.class);

                        roomList.add(model);

                        if (bind.refresh.isRefreshing()){
                            bind.refresh.setRefreshing(false);
                            Toast.makeText(requireContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                roomListAdapter = new RoomListAdapter(getActivity(), roomList);
                bind.scheduleMeetingRecyclerView.setAdapter(roomListAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Room List Error", "onErrorResponse: " +error);
            }
        });

        requestQueue.add(arrayRequest);

    }

    private void employeeFunction() {
        toolbar.setTitle("Employee Dashboard");
        toolbar.setNavigationIcon(null);
        navigationView.setVisibility(View.GONE);
        bind.createMeetingBtn.setVisibility(View.GONE);

        bind.scheduleMeetingRecyclerView.setVisibility(View.VISIBLE);
        bind.userViewLayout.setVisibility(View.VISIBLE);


        bind.showAdminViewMeeting.setVisibility(View.GONE);
        bind.scheduleMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        getScheduleMeetings();

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getScheduleMeetings();

            }
        });

    }

    private void getScheduleMeetings() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.MEETING_LIST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("res", "onResponse: "+response);

                try {
                    JSONArray meeting =response.getJSONArray("scheduleMeeting");
                    list.clear();


                    for (int i=0;i<meeting.length();i++){
                        JSONObject object=meeting.getJSONObject(i);
                        ScheduleMeetingModel model = new Gson().fromJson(object.toString(),ScheduleMeetingModel.class);
                        list.add(model);
                        bind.refresh.setRefreshing(false);


                    }

                    if (bind.refresh.isRefreshing()){
                        bind.refresh.setRefreshing(false);
                    }

                    adapter = new ScheduleMeetingAdapter(getActivity(), list);
                    bind.scheduleMeetingRecyclerView.setAdapter(adapter);
                    bind.scheduleMeetingRecyclerView.invalidate();
                    bind.scheduleMeetingRecyclerView.removeAllViews();



                } catch (JSONException e) {
                    throw new RuntimeException(e);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }


}