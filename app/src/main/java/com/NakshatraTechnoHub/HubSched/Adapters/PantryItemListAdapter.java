package com.NakshatraTechnoHub.HubSched.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.NakshatraTechnoHub.HubSched.Models.OrderPlaceModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryItemModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PantryItemListAdapter extends RecyclerView.Adapter<PantryItemListAdapter.RoomHolder> {

    private Context context;
    private String meetId;
    private List<PantryItemModel> itemList;
    private List<OrderPlaceModel> selectedItems;
    private OrderListener orderListener;

    public PantryItemListAdapter(Context context, String meetId, List<PantryItemModel> itemList) {
        this.context = context;
        this.meetId = meetId;
        this.itemList = itemList;
        this.selectedItems = new ArrayList<>(); // Initialize the selectedItems list
    }

    public void setOrderListener(OrderListener orderListener) {
        this.orderListener = orderListener;
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
        holder.totalQty.setText(String.valueOf(item.getQuantity()));

        // Set the background tint of the orderLayout based on the quantity
        if (item.getQuantity() > 0) {
            holder.orderLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orderedColor)));
        } else {
            holder.orderLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white)));
        }

        holder.addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = item.getQuantity();
                item.setQuantity(quantity + 1);
                holder.totalQty.setText(String.valueOf(item.getQuantity()));
                if (item.getQuantity() > 0) {
                    // Add the item to the selectedItems list if quantity is greater than 0
                    boolean isExisting = false;
                    for (OrderPlaceModel orderPlaceModel : selectedItems) {
                        if (orderPlaceModel.getItemName().equals(item.getItemName())) {
                            orderPlaceModel.setQuantity(item.getQuantity());
                            orderPlaceModel.setDescription(holder.description.getText().toString());
                            isExisting = true;
                            break;
                        }
                    }
                    if (!isExisting) {
                        OrderPlaceModel orderPlaceModel = new OrderPlaceModel(meetId,item.getItemName(), item.getQuantity(), holder.description.getText().toString());
                        selectedItems.add(orderPlaceModel);
                    }
                }

                // Change the background tint of the orderLayout
                holder.orderLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orderedColor)));
            }
        });

        holder.removeQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = item.getQuantity();
                if (quantity > 0) {
                    item.setQuantity(quantity - 1);
                    holder.totalQty.setText(String.valueOf(item.getQuantity()));
                    if (item.getQuantity() == 0) {
                        // Remove the item from the selectedItems list if quantity becomes 0
                        for (OrderPlaceModel orderPlaceModel : selectedItems) {
                            if (orderPlaceModel.getItemName().equals(item.getItemName())) {
                                selectedItems.remove(orderPlaceModel);
                                break;
                            }
                        }
                    } else {
                        // Update the quantity and description of the item in the selectedItems list
                        for (OrderPlaceModel orderPlaceModel : selectedItems) {
                            if (orderPlaceModel.getItemName().equals(item.getItemName())) {
                                orderPlaceModel.setQuantity(item.getQuantity());
                                orderPlaceModel.setDescription(holder.description.getText().toString());
                                break;
                            }
                        }
                    }
                }

                // Set the background tint of the orderLayout based on the updated quantity
                if (item.getQuantity() > 0) {
                    holder.orderLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orderedColor)));
                } else {
                    holder.orderLayout.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white)));
                }
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

    public interface OrderListener {
        void onOrderListUpdated(List<OrderPlaceModel> selectedItems);
    }

    public class RoomHolder extends RecyclerView.ViewHolder {

        TextView name, totalQty;
        ImageView addQty, removeQty;
        MaterialCardView orderLayout;
        EditText description;

        public RoomHolder(@NonNull View item) {
            super(item);
            name = item.findViewById(R.id.item_name);
            totalQty = item.findViewById(R.id.qty_view);
            addQty = item.findViewById(R.id.add_qty);
            removeQty = item.findViewById(R.id.remove_qty);
            description = item.findViewById(R.id.description_ed);
            orderLayout = item.findViewById(R.id.order_layout);
        }
    }
}
