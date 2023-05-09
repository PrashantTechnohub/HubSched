package com.NakshatraTechnoHub.HubSched.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.RoomListModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.button.MaterialButton;

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
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_room_list, parent, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListAdapter.RoomHolder holder, int position) {

        String id = String.valueOf(roomList.get(position).getRoom_no());

        holder.roomId.setText(id);
        holder.roomName.setText(roomList.get(position).getRoom_name());
        holder.roomSeats.setText(roomList.get(position).getSeat_cap()+"");
        holder.roomFloor.setText(roomList.get(position).getFloor_no());
        holder.roomFacilities.setText(roomList.get(position).getFacilities());

        SharedPreferences sh = holder.roomFacilities.getContext().getSharedPreferences("userTypeToken",MODE_PRIVATE);
        String type = sh.getString("type", "");

        if (type.equals("organiser")){
            holder.removeMeeting.setVisibility(View.GONE);

        }



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
