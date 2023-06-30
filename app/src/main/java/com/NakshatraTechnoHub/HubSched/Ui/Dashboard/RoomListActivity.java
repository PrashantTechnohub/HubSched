package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityRoomListBinding;
import com.google.gson.Gson;

import org.json.JSONArray;

public class RoomListActivity extends BaseActivity {

    ActivityRoomListBinding bind;
    String start;
    MyAdapter<RoomListModel> adapter;
    ArrayList<RoomListModel> roomList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRoomListBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        bind.pd.setVisibility(View.VISIBLE);

        bind.addRoom.setOnClickListener(v -> startActivity(new Intent(RoomListActivity.this, CreateRoomActivity.class)));

        bind.back.setOnClickListener(v -> finish());

        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bind.noResult.setVisibility(View.GONE);
                getRoomList();
            }
        });

        getRoomList();

    }

    private void getRoomList() {
        new Receiver(RoomListActivity.this, new Receiver.ListListener() {
            @Override
            public void onResponse(JSONArray response) {
                roomList.clear();

                if (response != null){
                    for (int i = 0; i<response.length(); i++){
                        try {
                            JSONObject object = response.getJSONObject(i);
                            RoomListModel model = new Gson().fromJson(object.toString(),RoomListModel.class);
                            roomList.add(model);
                            bind.pd.setVisibility(View.GONE);

                            if (bind.refresh.isRefreshing()){
                                bind.refresh.setRefreshing(false);
                                Toast.makeText(RoomListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            bind.pd.setVisibility(View.GONE);
                            ErrorHandler.handleException(getApplicationContext(), e);
                        }
                    }
                    adapter = new MyAdapter<>(roomList, new MyAdapter.OnBindInterface() {
                        @Override
                        public void onBindHolder(MyAdapter.MyHolder holder, int position) {

                            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
                            holder.itemView.startAnimation(animation);

                            TextView roomNo = holder.itemView.findViewById(R.id.room_no);
                            TextView roomName = holder.itemView.findViewById(R.id.room_name);
                            TextView roomSeats = holder.itemView.findViewById(R.id.room_seats);
                            TextView roomFacilities = holder.itemView.findViewById(R.id.room_facilities);
                            TextView roomFloor = holder.itemView.findViewById(R.id.room_floor);

                            MaterialButton setMeeting = holder.itemView.findViewById(R.id.schedule_meeting_btn);
                            MaterialButton removeRoom = holder.itemView.findViewById(R.id.remove_room_btn);
                            TextView view = holder.itemView.findViewById(R.id.view);

                            roomNo.setText(roomList.get(position).getRoom_no()+"");
                            roomName.setText(roomList.get(position).getRoom_name());
                            roomSeats.setText(roomList.get(position).getSeat_cap()+"");
                            roomFloor.setText(roomList.get(position).getFloor_no());

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });

                            String[] facilitiesArray = roomList.get(position).getFacilities();
                            ArrayList<String> facilities = new ArrayList<>(Arrays.asList(facilitiesArray));

                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < facilities.size(); i++) {
                                sb.append(i + 1).append(". ").append(facilities.get(i));
                                if (i != facilities.size() - 1) {
                                    sb.append("\n");
                                }
                            }

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                        Boolean isTrue = roomFacilities.isSingleLine();

                                        if (isTrue){
                                            view.setText("Less");
                                            roomFacilities.setSingleLine(false);
                                        }else{
                                            view.setText("More");
                                            roomFacilities.setSingleLine(true);
                                        }
                                    }
                                }
                            });

                            roomFacilities.setText(sb.toString());
                            SharedPreferences sh = roomFacilities.getContext().getSharedPreferences("userTypeToken", MODE_PRIVATE);
                            String type = sh.getString("type", "");

                            if (type.equals("organiser")){
                                removeRoom.setVisibility(View.GONE);
                            }

                            removeRoom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Are you sure to remove " + roomList.get(position).getRoom_name() + " Room !!");

                                    builder.setTitle("Confirm " + roomList.get(position).getRoom_no());
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removeRoom(roomList.get(position).get_id());
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); // Dismiss the dialog
                                        }
                                    });
                                    builder.create().show();
                                }
                            });

                            Calendar c = Calendar.getInstance();

                            int mYear = c.get(Calendar.YEAR); // current year
                            int mMonth = c.get(Calendar.MONTH); // current month
                            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                            setMeeting.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String id = String.valueOf(roomList.get(position).get_id());

                                    Toast.makeText(view.getContext(), id, Toast.LENGTH_SHORT).show();

                                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            String toDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                            start = toDate;

                                            Intent intent = new Intent(view.getContext(), CreateMeetingActivity.class);
                                            intent.putExtra("selectedDate", toDate);
                                            intent.putExtra("roomName", roomList.get(position).getRoom_name());
                                            intent.putExtra("roomId", id);
                                            view.getContext().startActivity(intent);
                                        }
                                    }, mYear, mMonth, mDay);
                                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                                    datePickerDialog.show();
                                }
                            });



                        }
                    }, R.layout.cl_room_list);
                    bind.roomListRecyclerView.setLayoutManager(new LinearLayoutManager(RoomListActivity.this));
                    bind.roomListRecyclerView.setAdapter(adapter);
                    bind.pd.setVisibility(View.GONE);

                }else{
                    bind.pd.setVisibility(View.GONE);
                    bind.noResult.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(VolleyError error) {
                bind.pd.setVisibility(View.GONE);
                bind.noResult.setVisibility(View.VISIBLE);
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).getRoomList();
    }

    private void removeRoom(Integer id) {
        new Receiver(RoomListActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                finish();
                getRoomList();
                Toast.makeText(RoomListActivity.this, "Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).remove_room(id);
    }
}