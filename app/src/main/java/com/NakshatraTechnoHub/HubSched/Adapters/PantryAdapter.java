package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Interface.ApiInterface;
import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {
    Context context;
    ArrayList<PantryModel> pantryList;

    static ApiInterface mHandler;

    public static void setHandler(ApiInterface handler) {
        mHandler = handler;
    }

    public PantryAdapter(Context context, ArrayList<PantryModel> pantryList) {
        this.context = context;
        this.pantryList = pantryList;
    }

    @NonNull
    @Override
    public PantryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_orderedlist, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PantryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.room_no.setText(pantryList.get(position).getRoomAddress());
        holder.name.setText(pantryList.get(position).getOrderedBy());
        holder.orderNo.setText("#Order: "+String.valueOf(position + 1)); // Set the order number

        if (!pantryList.get(position).getStatus().equals("requested")) {
            holder.ad_layout.setVisibility(View.GONE);
            holder.orderStatus.setVisibility(View.VISIBLE);
            holder.orderStatus.setText(pantryList.get(position).getStatus());

        } else {
            holder.orderStatus.setVisibility(View.GONE);
            holder.ad_layout.setVisibility(View.VISIBLE);

        }


        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderInDialog(position);

            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.clickHandler(pantryList.get(position).get_id(), true, position);

            }
        });

        holder.denyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.clickHandler(pantryList.get(position).get_id(), false, position);
            }
        });
    }

    private void showOrderInDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_dialogebox_pantry, null);
        builder.setView(team);

        TextView textViewItem = team.findViewById(R.id.textViewItem);

        JSONArray jsonArray = pantryList.get(position).getJsonArray();// Replace "response" with your actual JSON response object

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

    @Override
    public int getItemCount() {
        return pantryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView room_no, name, orderStatus, orderNo;

        LinearLayout ad_layout;
        MaterialButton acceptBtn, denyBtn, showOrderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            room_no = (itemView).findViewById(R.id.room_no);
            name = (itemView).findViewById(R.id.name);
            orderStatus = (itemView).findViewById(R.id.order_status);
            ad_layout = (itemView).findViewById(R.id.ad_layout);

            acceptBtn = (itemView).findViewById(R.id.accept_order);
            denyBtn = (itemView).findViewById(R.id.deny_order);
            showOrderBtn = (itemView).findViewById(R.id.show_order);
            orderNo = (itemView).findViewById(R.id.order_no);
        }
    }
}