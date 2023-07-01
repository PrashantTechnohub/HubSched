package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Adapters.ImageSliderAdapter;
import com.NakshatraTechnoHub.HubSched.Adapters.UpComingAdapter;
import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentHomeBinding;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ArrayList<FilteredMeetingModel> filteredMeetings = new ArrayList<>();


    private ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    private ArrayList<RoomListModel> roomList = new ArrayList<>();
    private FragmentHomeBinding bind;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private ImageSliderAdapter adapter;
    UpComingAdapter UpAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = bind.getRoot();

        toolbar = getActivity().findViewById(R.id.topAppBar);
        navigationView = getActivity().findViewById(R.id.navigation_view);
        String type = LocalPreference.getType(getContext());

        UpAdapter = new UpComingAdapter(filteredMeetings);

        bind.showMore.setOnClickListener(v -> {
            if (UpAdapter.areAllItemsVisible()) {
                bind.showMore.setText("Show More");
                UpAdapter.hideAdditionalItems();
            } else {
                bind.showMore.setText("Hide");

                UpAdapter.showAllItems();
                // Scroll to the last item in the NestedScrollView
                new Handler().postDelayed(() -> {
                    bind.srollView.fullScroll(View.FOCUS_DOWN);
                }, 200);
            }
        });




        bind.upcomingMeetingRecyclerview.setNestedScrollingEnabled(false);
        bind.refresh.setDistanceToTriggerSync(700);
        getBanners();
        autoScrollViewPager();
        upComingMeetings();

        if (type.equals("admin")) {
            adminFunction();
        } else if (type.equals("employee") || type.equals("scanner")) {
            employeeFunction();
        } else if (type.equals("organiser")) {
            organizerFunction();
        }

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBanners();
                autoScrollViewPager();
                upComingMeetings();

                if (type.equals("admin")) {
                    adminFunction();
                } else if (type.equals("employee")) {
                    employeeFunction();
                }
            }
        });





        return rootView;
    }

    private void upComingMeetings() {
        if (isAdded()) {
            Context context = requireContext();
            new Receiver(context, new Receiver.ListListener() {
                @Override
                public void onResponse(JSONArray array) {

                    // Get the current date
                    filteredMeetings.clear();
                    Calendar currentDate = Calendar.getInstance();

                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject meeting = array.getJSONObject(i);
                            JSONArray acceptedArray = meeting.getJSONArray("accepted");

                            // Check if "accepted" array contains your _id
                            boolean containsId = false;
                            for (int j = 0; j < acceptedArray.length(); j++) {
                                if (acceptedArray.getInt(j) == Integer.parseInt(LocalPreference.get_Id(context))) {
                                    containsId = true;
                                    break;
                                }
                            }

                            // Check if "date" is greater than or equal to the current date
                            String dateString = meeting.getString("date");
                            SimpleDateFormat format = new SimpleDateFormat("M/dd/yyyy", Locale.US);
                            Date meetingDate = format.parse(dateString);

                            if (containsId && meetingDate != null && meetingDate.compareTo(currentDate.getTime()) >= 0) {
                                // Get subject, location, and time as strings
                                String subject = meeting.getString("subject");
                                String date = meeting.getString("date");
                                String startTime = meeting.getString("startTime");
                                String endTime = meeting.getString("endTime");
                                String time = startTime + " - " + endTime;

                                // Create a new FilteredMeetingModel instance and add it to the filteredMeetings list
                                FilteredMeetingModel filteredMeeting = new FilteredMeetingModel(subject, date, time);
                                filteredMeetings.add(filteredMeeting);

                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    // Use the filteredMeetings list as needed
                    if (filteredMeetings != null && !filteredMeetings.isEmpty()) {

                        // Set the number of spans (2 in your case)

                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                        bind.upcomingMeetingRecyclerview.setLayoutManager(layoutManager);
                        bind.upcomingMeetingRecyclerview.setAdapter(UpAdapter);



                    } else {

                        bind.upcomingMeetingRecyclerview.setVisibility(View.GONE);
                        bind.textUp.setVisibility(View.GONE);

                    }




                }

                @Override
                public void onError(VolleyError error) {
                    // Handle error here
                }
            }).getMeetingList();
        }
    }


    private void autoScrollViewPager() {
        // Check if the adapter is null
        if (adapter == null) {
            // Handle the null adapter case
            return;
        }

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Check if the imageSlider is null or the adapter is not set
            if (bind.imageSlider != null && bind.imageSlider.getAdapter() != null) {
                bind.imageSlider.setCurrentItem((bind.imageSlider.getCurrentItem() + 1) % adapter.getCount());
            }
            autoScrollViewPager();
        }, 2500); // Auto-scroll interval in milliseconds
    }


    private void getBanners() {
        if (isAdded()) {
            Context context = requireContext();
            new Receiver(context, new Receiver.ApiListener() {
                @Override
                public void onResponse(JSONObject object) {
                    try {
                        List<String> containList = new ArrayList<>();

                        JSONArray banners = object.getJSONArray("banners");
                        for (int i = 0; i < banners.length(); i++) {
                            JSONObject banner = banners.getJSONObject(i);

                            String contain = banner.getString("contain");
                            containList.add(contain);

                        }


                        bitmapList.clear();
                        bitmapList = decodeBase64ToBitmapList(containList);

                        adapter = new ImageSliderAdapter(bitmapList, context);
                        bind.imageSlider.setAdapter(adapter);


                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(VolleyError error) {
                    // Handle error here
                }
            }).banner_list(new JSONObject());
        }
    }

    private ArrayList<Bitmap> decodeBase64ToBitmapList(List<String> base64StringList) {
        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        for (String base64String : base64StringList) {
            try {
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                bitmapList.add(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return bitmapList;
    }


    public void adminFunction() {
        toolbar.setTitle("Admin Dashboard");

        bind.totalEmployees.setText(LocalPreference.get_total_employees(requireContext()));
        bind.totalMeetings.setText(LocalPreference.get_total_meetings(requireContext()));
        bind.totalRooms.setText(LocalPreference.get_total_rooms(requireContext()));

        if (bind.refresh.isRefreshing()) {
            bind.refresh.setRefreshing(false);
            Toast.makeText(requireContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    public void organizerFunction() {
        bind.adminViewLayout.setVisibility(View.GONE);
        toolbar.setTitle("Organizer Dashboard");
        toolbar.setNavigationIcon(null);
    }

    public void employeeFunction() {
        toolbar.setTitle("Employee Dashboard");
        toolbar.setNavigationIcon(null);
        navigationView.setVisibility(View.GONE);
        bind.adminViewLayout.setVisibility(View.GONE);

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do something for employee refresh
            }
        });
    }
}
