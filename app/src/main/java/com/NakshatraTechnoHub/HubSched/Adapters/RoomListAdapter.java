package com.NakshatraTechnoHub.HubSched.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateMeetingActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.databinding.ClDateTimeBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomHolder> {

    Context context;

    String start;
    String end;

    ArrayList<RoomListModel> roomList = new ArrayList<>();

    public RoomListAdapter(Context context, ArrayList<RoomListModel> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomListAdapter.RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_room_list, parent, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListAdapter.RoomHolder holder, @SuppressLint("RecyclerView") int position) {

        String id = String.valueOf(roomList.get(position).get_id());


        holder.roomNo.setText(roomList.get(position).getRoom_no()+"");
        holder.roomName.setText(roomList.get(position).getRoom_name());
        holder.roomName.setText(roomList.get(position).getRoom_name());
        holder.roomSeats.setText(roomList.get(position).getSeat_cap()+"");
        holder.roomFloor.setText(roomList.get(position).getFloor_no());

        holder.view.setOnClickListener(new View.OnClickListener() {
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

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    Boolean isTrue = holder.roomFacilities.isSingleLine();

                    if (isTrue){
                        holder.view.setText("Less");
                        holder.roomFacilities.setSingleLine(false);
                    }else{
                        holder.view.setText("More");
                        holder.roomFacilities.setSingleLine(true);

                    }
                }

            }
        });

        holder.roomFacilities.setText(sb.toString());
        SharedPreferences sh = holder.roomFacilities.getContext().getSharedPreferences("userTypeToken",MODE_PRIVATE);
        String type = sh.getString("type", "");

        if (type.equals("organiser")){
            holder.removeRoom.setVisibility(View.GONE);

        }

        holder.removeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure to remove "+roomList.get(position).getRoom_name() + " Room !!");

                builder.setTitle("Confirm " +    roomList.get(position).getRoom_no());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeRoom( roomList.get(position).get_id());

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


        holder.setMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(roomList.get(position).get_id());

                Toast.makeText(view.getContext(), id, Toast.LENGTH_SHORT).show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {



                        String toDate = (monthOfYear + 1)  + "/" + dayOfMonth  + "/" + year;
                        start = toDate ;

                        Intent intent = new Intent(view.getContext(), CreateMeetingActivity.class);
                        intent.putExtra("selectedDate", toDate);
                        intent.putExtra("roomName", roomList.get(position).getRoom_name());
                        intent.putExtra("roomId", id);
                        view.getContext(). startActivity(intent);

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();

            }
        });


    }

    private void removeRoom(Integer id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.DELETE, Constant.withToken(Constant.REMOVE_ROOM_URL+(id+""),context), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status=    response.getString("message");
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();

                    notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    if(error.networkResponse.statusCode == 500){
                        String errorString = new String(error.networkResponse.data);
                        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        queue.add(objectRequest);
    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {

        TextView roomNo, roomName, roomSeats, roomFacilities, roomFloor, view;

        MaterialButton setMeeting, removeRoom;


        public RoomHolder(@NonNull View room) {
            super(room);

            roomNo = room.findViewById(R.id.room_no);
            roomName = room.findViewById(R.id.room_name);
            roomSeats = room.findViewById(R.id.room_seats);
            roomFacilities = room.findViewById(R.id.room_facilities);
            roomFloor = room.findViewById(R.id.room_floor);

            setMeeting = room.findViewById(R.id.schedule_meeting_btn);
            removeRoom = room.findViewById(R.id.remove_room_btn);
            view = room.findViewById(R.id.view);


        }
    }
}
