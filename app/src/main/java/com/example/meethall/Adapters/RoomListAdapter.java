package com.example.meethall.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meethall.Models.RoomListModel;
import com.example.meethall.R;
import com.example.meethall.UserChecker.checkUserDetailPreference;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomHolder> {

    Context context;
    ArrayList<RoomListModel> roomList = new ArrayList<>();

    public RoomListAdapter(Context context, ArrayList<RoomListModel> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomListAdapter.RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_layout, parent, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListAdapter.RoomHolder holder, int position) {

        String id = String.valueOf(roomList.get(position).getRoom_id());

        holder.roomId.setText(id);
        holder.roomName.setText(roomList.get(position).getRoom_name());
        holder.roomSeats.setText(roomList.get(position).getSeat_cap());
        holder.roomFloor.setText(roomList.get(position).getFloor_no());
        holder.roomFacilities.setText(roomList.get(position).getFacilities());

        SharedPreferences sh = holder.roomFacilities.getContext().getSharedPreferences("userTypeToken",MODE_PRIVATE);
        String type = sh.getString("type", "");

        if (type.equals("organiser")){
            holder.removeMeeting.setVisibility(View.GONE);

        }

        holder.setMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(holder.roomFacilities.getContext(), R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) holder.roomFacilities.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.create_meeting_layout, null);
                builder.setView(team);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.show();

                ImageView close = team.findViewById(R.id.create_meet_close_dialog);
                MaterialButton create = team.findViewById(R.id.create_meet_btn);
                EditText roomNametxt = team.findViewById(R.id.create_meet_name);
                EditText roomSubtxt = team.findViewById(R.id.create_meet_sub);
                EditText roomTimetxt = team.findViewById(R.id.create_meet_time);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });



    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {

        TextView roomId, roomName, roomSeats, roomFacilities, roomFloor;

        MaterialButton setMeeting, removeMeeting;


        public RoomHolder(@NonNull View room) {
            super(room);

            roomId = room.findViewById(R.id.room_id);
            roomName = room.findViewById(R.id.room_name);
            roomSeats = room.findViewById(R.id.room_seats);
            roomFacilities = room.findViewById(R.id.room_facilities);
            roomFloor = room.findViewById(R.id.room_floor);

            setMeeting = room.findViewById(R.id.schedule_meeting_btn);
            removeMeeting = room.findViewById(R.id.remove_meeting_btn);


        }
    }
}
