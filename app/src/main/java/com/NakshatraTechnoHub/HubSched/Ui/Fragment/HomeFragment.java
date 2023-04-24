package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.NakshatraTechnoHub.HubSched.Adapters.ScheduleMeetingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateMeetingActivity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { bind = FragmentHomeBinding.inflate(inflater);

        toolbar = (MaterialToolbar) requireActivity().findViewById(R.id.topAppBar);
        navigationView = requireActivity().findViewById(R.id.navigation_view);

        SharedPreferences sh = getActivity().getSharedPreferences("userTypeToken",MODE_PRIVATE);
        String type = sh.getString("type", "");

        requestQueue = Volley.newRequestQueue(requireActivity());

        if (type.equals("admin")) {
            adminFunction();
        } else if (type.equals("employee")) {
            employeeFunction();
        } else if (type.equals("organiser")) {
            organizerFunction();
        }



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

    }

    private void organizerFunction() {

        bind.imageSlider.setVisibility(View.GONE);
        toolbar.setTitle("Organizer Dashboard");
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.hallManagement).setVisible(false);
        nav_Menu.findItem(R.id.editContent).setVisible(false);
        nav_Menu.findItem(R.id.organizerList).setVisible(false);
        bind.scheduleMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        getRoomList();

    }

    private void getRoomList() {

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.MEET_ROOMS_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);

                        int roomId = object.getInt("room_id");
                        String roomName = object.getString("room_name");
                        String seatCapacity = object.getString("seat_cap");
                        String roomLocation = object.getString("floor_no");
                        String roomFacilities = object.getString("facilities");

                        RoomListModel model = new RoomListModel(roomId, roomLocation, roomName, seatCapacity, roomFacilities);

                        roomList.add(model);

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.SCHEDULED_MEETING_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("res", "onResponse: "+response);

                try {
                    JSONArray meeting =response.getJSONArray("scheduleMeeting");
                    list.clear();


                    for (int i=0;i<meeting.length();i++){
                        JSONObject object=meeting.getJSONObject(i);

                        String meetId=object.getString("meet_id");
                        String meetOrg=object.getString("meet_org");
                        String meetSub=object.getString("meet_subject");
                        String meetTime=object.getString("meet_time");
                        String meetLocation=object.getString("meet_location");

                        ScheduleMeetingModel model=new ScheduleMeetingModel(meetId,meetOrg, meetSub, meetTime, meetLocation, "nothing");

                        list.add(model);

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