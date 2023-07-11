package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.NakshatraTechnoHub.HubSched.Adapters.ImageSliderAdapter;
import com.NakshatraTechnoHub.HubSched.Adapters.UpComingAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.Models.ScheduleMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.InMeetingActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentHomeBinding;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    ArrayList<FilteredMeetingModel> filteredMeetings = new ArrayList<>();


    ScheduleMeetingModel ongoingMeeting = null;
    ArrayList<ScheduleMeetingModel> list = new ArrayList<>();
    private ArrayList<RoomListModel> roomList = new ArrayList<>();
    private FragmentHomeBinding bind;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    private ImageSliderAdapter adapter;
    UpComingAdapter UpAdapter;
    String type;
    MenuItem roomMeetingCreationMenu, manageMeetingMenu, employeeMenu, contentMenu, ticketMenu;

    Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = bind.getRoot();

        toolbar = getActivity().findViewById(R.id.topAppBar);


        navigationView = getActivity().findViewById(R.id.navigation_view);
        menu = navigationView.getMenu();
        employeeMenu = menu.findItem(R.id.employeeList);
        manageMeetingMenu = menu.findItem(R.id.meetingList);
        ticketMenu = menu.findItem(R.id.queries);
        roomMeetingCreationMenu = menu.findItem(R.id.hallManagement);
        contentMenu = menu.findItem(R.id.editContent);




        type = LocalPreference.getType(getContext());


        bind.rrl1.setVisibility(View.VISIBLE);
        bind.invitationLayout.setVisibility(View.VISIBLE);

        UpAdapter = new UpComingAdapter(filteredMeetings);

        bind.announceText.setSelected(true);

        boolean is7 = UpAdapter.showAllItems();

        if (is7) {
            bind.showMore.setVisibility(View.VISIBLE);
        } else {
            bind.showMore.setVisibility(View.GONE);
        }
        bind.showMore.setOnClickListener(v -> {
            if (UpAdapter.areAllItemsVisible()) {
                bind.showMore.setText("Show More");
                UpAdapter.hideAdditionalItems();
            } else {
                bind.showMore.setText("Hide");

                UpAdapter.showAllItems();
                new Handler().postDelayed(() -> {
                    bind.scrollView.fullScroll(View.FOCUS_DOWN);
                }, 200);
            }
        });


        bind.upcomingMeetingRecyclerview.setNestedScrollingEnabled(false);
        getBanners();
        getInvitationList();
        autoScrollViewPager();
        upComingMeetings();

        if (type.equals("admin")) {
            adminFunction();
        } else if (type.equals("employee") || type.equals("scanner")) {
            employeeFunction();
        } else if (type.equals("organiser")) {
            organizerFunction();
        }


        return rootView;
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap decodeBase64ToBitmapList(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            return bitmap;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_menu, menu);
        MenuItem refresh = menu.findItem(R.id.refresh);

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getInvitationList();
                getBanners();
                upComingMeetings();
                Toast.makeText(requireContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void getInvitationList() {
        if (isAdded()) {
            // Fragment is added to the activity
            // Access the context or perform other operations that require the fragment to be attached
            Context context = requireContext();
            // Rest of your code here

            new Receiver(context, new Receiver.ListListener() {
                @Override
                public void onResponse(JSONArray response) {
                    ongoingMeeting = null;
                    list.clear();
                    if (response != null && response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                ScheduleMeetingModel model = new Gson().fromJson(object.toString(), ScheduleMeetingModel.class);

                                if (!model.getAccepted().contains(Integer.parseInt(LocalPreference.get_Id(context))) && !model.getDeclined().contains(Integer.parseInt(LocalPreference.get_Id(context)))) {
                                    list.add(model);
                                }
                                Date now = new Date();
                                Date meetDate = getDateFromString(model.getDate() + " " + model.getStartTime());
                                Date endDate = getDateFromString(model.getDate() + " " + model.getEndTime());
                                if (meetDate != null && model.getAccepted().contains(Integer.parseInt(LocalPreference.get_Id(context))) && now.after(meetDate) && !now.after(endDate)) {
                                    ongoingMeeting = model;
                                }


                            } catch (JSONException e) {
                                ErrorHandler.handleException(requireContext(), e);

                            }
                        }

                        if (list.size() == 0)
                            bind.invitationLayout.setVisibility(View.GONE);

                    } else {
                        bind.invitationLayout.setVisibility(View.GONE);
                    }

                    if (ongoingMeeting != null) {
                        bind.ongoingLayout.setVisibility(View.VISIBLE);
                        bind.upSubject.setText(ongoingMeeting.getSubject());
                        bind.gotoOngoingMeeting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                generateQrCode(ongoingMeeting.get_id() + "", ongoingMeeting.getCompany_id() + "", ongoingMeeting.getSubject(), ongoingMeeting.getStartTime(), ongoingMeeting.getEndTime(), ongoingMeeting.getDate());
                            }
                        });
                    } else {
                        bind.ongoingLayout.setVisibility(View.GONE);
                        bind.gotoOngoingMeeting.setOnClickListener(null);
                    }
                    bind.invitationMeetingRecyclerview.setAdapter(new MyAdapter<ScheduleMeetingModel>(list, new MyAdapter.OnBindInterface() {
                        @Override
                        public void onBindHolder(MyAdapter.MyHolder holder, int position) {

                            TextView orgName = holder.itemView.findViewById(R.id.meet_orgName);
                            TextView meetSubject = holder.itemView.findViewById(R.id.meet_subject);
                            TextView meetTime = holder.itemView.findViewById(R.id.meet_time);
                            MaterialButton denyBtn = holder.itemView.findViewById(R.id.meet_deny_btn);
                            MaterialButton acptBtn = holder.itemView.findViewById(R.id.meet_accept_btn);


                            String inputDate = list.get(position).getDate();
                            SimpleDateFormat inputFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                            try {
                                Date date = inputFormat.parse(inputDate);
                                String formattedDate = outputFormat.format(date);
                                orgName.setText(list.get(position).getOrganiser_name() + "");
                                meetSubject.setText(list.get(position).getSubject());
                                meetTime.setText(list.get(position).getStartTime() + " - " + list.get(position).getEndTime() + " " + list.get(position).getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            acptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    acceptDenied(list.get(position).get_id(), true, position);

                                }
                            });
                            denyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    acceptDenied(list.get(position).get_id(), false, position);

                                }
                            });


                        }
                    }, R.layout.cl_meeting_invitation));


                }

                @Override
                public void onError(VolleyError error) {


                    ErrorHandler.handleVolleyError(getActivity(), error);
                }
            }).getMeetingList();

        }


    }


    Date getDateFromString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy hh:mm a", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Set the time zone to Kolkata
        try {
            Date endDate = format.parse(dateString);
            return endDate;
        } catch (ParseException e) {
            ErrorHandler.handleException(requireContext(), e);
            return null;
        }

    }

    private void acceptDenied(int meetId, Boolean ready, int pos) {
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
                    list.remove(pos);
                    bind.invitationMeetingRecyclerview.getAdapter().notifyItemRemoved(pos);

                    if (pd.isDialogShown()) {
                        pd.mDismiss();
                    }
                } catch (JSONException e) {
                    ErrorHandler.handleException(getContext(), e);
                    pd.mDismiss();
                }
            }

            @Override
            public void onError(VolleyError error) {
                pd.mDismiss();
                ErrorHandler.handleVolleyError(requireActivity(), error);

            }
        }).accept_denied_meeting(params);

    }


    private void generateQrCode(String getMeetId, String companyId, String subject, String startTime, String endTime, String date) {

        JSONObject params = new JSONObject();

        try {
            params.put("meetId", Integer.parseInt(getMeetId));

        } catch (JSONException e) {

            e.printStackTrace();
        }
        Context context = requireContext();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.GENERATE_QR_CODE_URL, context), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(context, InMeetingActivity.class);
                intent.putExtra("response", response.toString());
                intent.putExtra("meetId", getMeetId);
                intent.putExtra("companyId", companyId);
                intent.putExtra("subject", subject);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", date + " " + endTime);
                LocalPreference.store_meetId(context, getMeetId);
                context.startActivity(intent);
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 500) {

                            String errorString = new String(error.networkResponse.data);
                            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(context, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Log.e("CreateEMP", "onErrorResponse: ", e);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        VolleySingleton.getInstance(context).addToRequestQueue(request);


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
                            String dateString = meeting.getString("date") + " " + meeting.getString("startTime");
                            SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy hh:mm a", Locale.US);
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

                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                        bind.upcomingMeetingRecyclerview.setLayoutManager(layoutManager);
                        bind.upcomingMeetingRecyclerview.setAdapter(UpAdapter);


                    } else {
                        filteredMeetings.clear();
                        bind.rrl1.setVisibility(View.GONE);

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
                int adapterCount = adapter.getCount();
                if (adapterCount != 0) {
                    bind.imageSlider.setCurrentItem((bind.imageSlider.getCurrentItem() + 1) % adapterCount);
                }
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
                    List<String> containList = new ArrayList<>();

                    int totalMeeting = object.optInt("meetingSize");
                    int totalEmployee = object.optInt("members");
                    int totalRoom = object.optInt("roomSize");
                    int totalTicket = object.optInt("ticketSize");

                    // Set total integers in the text view
                    bind.totalRooms.setText(String.valueOf(totalRoom));
                    bind.totalEmployees.setText(String.valueOf(totalEmployee));
                    bind.totalMeetings.setText(String.valueOf(totalMeeting));
                    bind.totalBugs.setText(String.valueOf(totalTicket));

                    JSONObject bannerList = object.optJSONObject("bannerList");
                    if (bannerList != null) {
                        JSONArray banners = bannerList.optJSONArray("banners");
                        if (banners != null) {
                            for (int i = 0; i < banners.length(); i++) {
                                JSONObject banner = banners.optJSONObject(i);
                                if (banner != null) {
                                    String contain = banner.optString("contain");
                                    containList.add(contain);
                                }
                            }
                        }
                    }


                    bitmapList.clear();
                    bitmapList = decodeBase64ToBitmapList(containList);

                    adapter = new ImageSliderAdapter(bitmapList, context);
                    bind.imageSlider.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(VolleyError error) {
                    ErrorHandler.handleVolleyError(context, error);
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
        bind.adminViewLayout.setVisibility(View.VISIBLE);

    }

    public void organizerFunction() {
        bind.adminViewLayout.setVisibility(View.GONE);

        ticketMenu.setVisible(false);
        employeeMenu.setVisible(false);
        contentMenu.setVisible(false);

        toolbar.setTitle("Organizer Dashboard");

    }

    public void employeeFunction() {
        toolbar.setTitle("Employee Dashboard");
        ticketMenu.setVisible(false);
        manageMeetingMenu.setVisible(false);
        roomMeetingCreationMenu.setVisible(false);
        employeeMenu.setVisible(false);
        contentMenu.setVisible(false);

        bind.adminViewLayout.setVisibility(View.GONE);
        bind.rrl1.setVisibility(View.VISIBLE);
        bind.invitationLayout.setVisibility(View.VISIBLE);
        getInvitationList();
        upComingMeetings();

    }
}
