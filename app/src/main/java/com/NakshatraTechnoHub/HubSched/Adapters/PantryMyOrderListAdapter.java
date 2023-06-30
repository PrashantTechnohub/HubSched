package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PantryMyOrderListAdapter extends RecyclerView.Adapter<PantryMyOrderListAdapter.ViewHolder> {
    Context context;

    Activity activity;
    private ArrayList<PantryModel> list;
    private ArrayList<PantryModel> filteredData;

    public PantryMyOrderListAdapter(Context context, Activity activity, ArrayList<PantryModel> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.filteredData = new ArrayList<>(list); // Initialize filteredData with the same data
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_orderedlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PantryModel model = list.get(position);


        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.item_animation);
        holder.itemView.startAnimation(animation);
        holder.orderStatus.setVisibility(View.VISIBLE);

        holder.orderNo.setText("#Order: " + String.valueOf(position + 1)); // Set the order number
        holder.orderStatus.setText(model.getStatus());
        holder.room_no.setText(model.getRoomAddress());
        holder.name.setText(model.getOrderedBy());

        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_dialogebox_pantry, null);
                builder.setView(team);

                TextView textViewItem = team.findViewById(R.id.textViewItem);

                JSONArray jsonArray = list.get(position).getJsonArray();// Replace "response" with your actual JSON response object

                StringBuilder itemQuantityBuilder = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(i);
                        String item = jsonObject.getString("itemName");
                        int quantity = jsonObject.getInt("quantity");

                        // Append the item and quantity to the StringBuilder
                        itemQuantityBuilder.append(item + "              " + quantity).append("\n\n");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                textViewItem.setText(itemQuantityBuilder.toString());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setData(ArrayList<PantryModel> newData) {
        list = newData;
        filterData(""); // Apply the filter with the active filter value and pass the activity reference
    }


    public void filterData(String status) {
        filteredData.clear();
        if (status.isEmpty()) {
            filteredData.addAll(list); // Display the entire list if the status is empty
        } else {
            for (PantryModel model : list) {
                if (model.getStatus().equals(status)) {
                    filteredData.add(model); // Add models with matching status to filteredData
                }
            }
        }

        // Check if the filteredData is empty
        if (filteredData.isEmpty()) {
            // Show empty state
            activity.findViewById(R.id.pd).setVisibility(View.GONE);
            activity.findViewById(R.id.no_result).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.no_result).setVisibility(View.GONE);

        }

        notifyDataSetChanged(); // Notify the adapter about the data change
    }


    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView room_no, orderNo, orderStatus, name;
        MaterialButton showOrderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            room_no = itemView.findViewById(R.id.room_no);
            orderNo = itemView.findViewById(R.id.order_no);
            name = itemView.findViewById(R.id.name);
            orderStatus = itemView.findViewById(R.id.order_status);
            showOrderBtn = itemView.findViewById(R.id.show_order);
        }
    }
}
