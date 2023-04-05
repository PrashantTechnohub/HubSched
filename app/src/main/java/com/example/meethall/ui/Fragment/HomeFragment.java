package com.example.meethall.ui.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.example.meethall.Adapters.EmpListAdapter;
import com.example.meethall.Adapters.RoomListAdapter;
import com.example.meethall.Adapters.ScheduleMeetingAdapter;
import com.example.meethall.Api.Constant;
import com.example.meethall.Models.EmpListModel;
import com.example.meethall.Models.RoomListModel;
import com.example.meethall.Models.ScheduleMeetingModel;
import com.example.meethall.R;
import com.example.meethall.databinding.FragmentHomeBinding;
import com.example.meethall.ui.Dashboard.CreateMeeting;
import com.example.meethall.ui.Dashboard.emp_list;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                getActivity() .startActivity(new Intent(requireActivity(), CreateMeeting.class));
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
        bind.createMeetingBtn.setVisibility(View.GONE);
        navigationView.setVisibility(View.GONE);
        bind.createMeetingBtn.setVisibility(View.GONE);
        bind.scheduleMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        getScheduleMeetings();
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