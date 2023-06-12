package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.OrderPlaceModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryItemModel;
import com.NakshatraTechnoHub.HubSched.R;

import java.util.ArrayList;
import java.util.List;

public class PantryItemListAdapter extends RecyclerView.Adapter<PantryItemListAdapter.RoomHolder> {

    private Context context;
    private String meetId;
    private List<PantryItemModel> itemList;
    private List<OrderPlaceModel> selectedItems;

    public PantryItemListAdapter(Context context, String meetId, List<PantryItemModel> itemList) {
        this.context = context;
        this.meetId = meetId;
        this.itemList = itemList;
        this.selectedItems = new ArrayList<>(); // Initialize the selectedItems list
    }

    @NonNull
    @Override
    public PantryItemListAdapter.RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_pantry_order, parent, false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryItemListAdapter.RoomHolder holder, @SuppressLint("RecyclerView") int position) {

        PantryItemModel item = itemList.get(position);
        holder.name.setText(item.getItemName());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (isChecked) {
                OrderPlaceModel orderPlaceModel = new OrderPlaceModel(meetId, item.getItemName(), item.getQuantity(), holder.description.getText().toString());
                selectedItems.add(orderPlaceModel);
            } else {
                OrderPlaceModel orderToRemove = null;
                for (OrderPlaceModel order : selectedItems) {
                    if (order.getItemName().equals(item.getItemName())) {
                        orderToRemove = order;
                        break;
                    }
                }
                if (orderToRemove != null) {
                    selectedItems.remove(orderToRemove);
                }
            }
        });

        holder.addQty.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            item.setQuantity(quantity + 1);
            holder.totalQty.setText(String.valueOf(item.getQuantity()));
        });

        holder.removeQty.setOnClickListener(v -> {
            int quantity = item.getQuantity();
            if (quantity > 0) {
                item.setQuantity(quantity - 1);
                holder.totalQty.setText(String.valueOf(item.getQuantity()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<OrderPlaceModel> getSelectedItems() {
        return selectedItems;
    }

    public class RoomHolder extends RecyclerView.ViewHolder {

        TextView name, totalQty;
        ImageView addQty, removeQty;
        EditText description;
        CheckBox checkBox;

        public RoomHolder(@NonNull View item) {
            super(item);
            name = item.findViewById(R.id.item_name);
            totalQty = item.findViewById(R.id.qty_view);
            addQty = item.findViewById(R.id.add_qty);
            removeQty = item.findViewById(R.id.remove_qty);
            description = item.findViewById(R.id.description_ed);
            checkBox = item.findViewById(R.id.checkbox);
        }
    }
}