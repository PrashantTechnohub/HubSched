package com.example.meethall.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meethall.Models.ScheduleMeetingModel;
import com.example.meethall.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ScheduleMeetingAdapter extends RecyclerView.Adapter<ScheduleMeetingAdapter.ViewHolder> {

    Context context;
    ArrayList<ScheduleMeetingModel> list;

    public ScheduleMeetingAdapter(Context context, ArrayList<ScheduleMeetingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ScheduleMeetingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleMeetingAdapter.ViewHolder holder, int position) {

        holder.orgName.setText(list.get(position).getOrgName());
        holder.meetSubject.setText(list.get(position).getMeetingSubject());
        holder.meetTime.setText(list.get(position).getMeetingTime());
        holder.meetLocation.setText(list.get(position).getMeetingLocation());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView orgName, meetSubject, meetTime, meetLocation;

        MaterialButton acptBtn, denyBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orgName = itemView.findViewById(R.id.meet_orgName);
            meetSubject = itemView.findViewById(R.id.meet_subject);
            meetTime = itemView.findViewById(R.id.meet_time);
            meetLocation = itemView.findViewById(R.id.meet_location);

            acptBtn = itemView.findViewById(R.id.meet_accept_btn);
            denyBtn = itemView.findViewById(R.id.meet_deny_btn);

        }
    }
}
