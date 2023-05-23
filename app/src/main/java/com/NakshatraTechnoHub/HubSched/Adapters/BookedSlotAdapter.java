package com.NakshatraTechnoHub.HubSched.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.BookedSlotModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.ArrayList;

public class BookedSlotAdapter extends RecyclerView.Adapter<BookedSlotAdapter.ViewHolder> {
    Context context;
    ArrayList<BookedSlotModel>list;

    public BookedSlotAdapter(Context context, ArrayList<BookedSlotModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BookedSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cl_create_meeting,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedSlotAdapter.ViewHolder holder, int position) {
        holder.startTime.setText(list.get(position).getStartTime());
        holder.endTime.setText(list.get(position).getEndTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startTime,endTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startTime=itemView.findViewById(R.id.start_time);
            endTime=itemView.findViewById(R.id.end_Time);
        }
    }
}
