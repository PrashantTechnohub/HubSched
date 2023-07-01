package com.NakshatraTechnoHub.HubSched.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.List;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.FilteredMeetingModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.ArrayList;
import java.util.List;

public class UpComingAdapter extends RecyclerView.Adapter<UpComingAdapter.ViewHolder> {

    private List<FilteredMeetingModel> filteredMeetings;
    private boolean showAllItems;

    public UpComingAdapter(List<FilteredMeetingModel> filteredMeetings) {
        this.filteredMeetings = filteredMeetings;
        this.showAllItems = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_upcoming_meetings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilteredMeetingModel meeting = filteredMeetings.get(position);
        holder.bind(meeting);
        if (!showAllItems &&  position == getItemCount() - 1) {
            holder.fadeItem();
        } else {
            holder.unfadeItem();
        }
    }

    @Override
    public int getItemCount() {
        if (showAllItems) {
            return filteredMeetings.size();
        } else {
            // Adjust the number of visible items as per your requirement
            return Math.min(filteredMeetings.size(), 6);
        }
    }

    public void showAllItems() {
        showAllItems = true;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView timeTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.up_subject);
            timeTextView = itemView.findViewById(R.id.up_time);
            dateTextView = itemView.findViewById(R.id.up_date);
        }

        public void bind(FilteredMeetingModel meeting) {
            subjectTextView.setText(meeting.getSubject());
            timeTextView.setText(meeting.getTime());
            dateTextView.setText(meeting.getDate());
        }

        public void fadeItem() {
            itemView.setAlpha(0.5f);
        }

        public void unfadeItem() {
            itemView.setAlpha(1.0f);
        }
    }



    public void hideAdditionalItems() {
        showAllItems = false;
        notifyDataSetChanged();
    }

    public boolean areAllItemsVisible() {
        return showAllItems;
    }
}
