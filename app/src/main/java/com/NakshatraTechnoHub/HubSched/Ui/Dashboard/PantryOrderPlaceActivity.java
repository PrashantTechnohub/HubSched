package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Adapters.PantryItemListAdapter;
import com.NakshatraTechnoHub.HubSched.Models.OrderPlaceModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryItemModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantryOrderPlaceActivity extends BaseActivity implements PantryItemListAdapter.OrderListener {

    private List<PantryItemModel> itemList;
    private RecyclerView recyclerView;
    private PantryItemListAdapter adapter;
    private SwipeRefreshLayout refresh;

    List<OrderPlaceModel> selectedItems = new ArrayList<>();
    MaterialButton placeOrder;
    String meetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_order_place);

        placeOrder = findViewById(R.id.place_order_button);
        recyclerView = findViewById(R.id.pantry_list_recyclerview);
        refresh = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        meetId = LocalPreference.get_meetId(this);

        findViewById(R.id.back).setOnClickListener(view -> finish());

        refresh.setOnRefreshListener(() -> getItemList());
        getItemList();

        placeOrder.setOnClickListener(view -> {
            List<OrderPlaceModel> selectedItems = adapter.getSelectedItems();
            showSelectedItemsDialog(selectedItems);
        });
    }

    private void showSelectedItemsDialog(List<OrderPlaceModel> selectedItems) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_selected_items, null);
        builder.setView(dialogView);

        // Get references to dialog views
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView contentTextView = dialogView.findViewById(R.id.dialog_content);

        // Set dialog title
        titleTextView.setText("Selected Items");

        // Prepare the content string with selected items
        StringBuilder contentBuilder = new StringBuilder();
        for (OrderPlaceModel item : selectedItems) {
            contentBuilder.append(item.getItemName()).append(" - ").append(item.getQuantity()).append("\n");
        }
        String content = contentBuilder.toString().trim();

        // Set the content text
        contentTextView.setText(content);

        builder.setPositiveButton("Place Order", (dialogInterface, i) -> postSelectedItems(selectedItems));

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void postSelectedItems(List<OrderPlaceModel> selectedItems) {
        // Create a JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("meetId", meetId);

            JSONArray itemsArray = new JSONArray();

            for (OrderPlaceModel item : selectedItems) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("itemName", item.getItemName());
                itemObject.put("quantity", item.getQuantity());
                itemsArray.put(itemObject);
            }
            requestBody.put("OrderedList", itemsArray);
        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }

        new Receiver(PantryOrderPlaceActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                finish();
                Toast.makeText(PantryOrderPlaceActivity.this, "Order Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);
            }
        }).post_item_to_pantry(requestBody);
    }

    private void getItemList() {
        itemList = new ArrayList<>();
        meetId = LocalPreference.get_meetId(PantryOrderPlaceActivity.this);

        new Receiver(PantryOrderPlaceActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                if (refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                    Toast.makeText(PantryOrderPlaceActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                }

                itemList.clear();

                try {
                    JSONArray itemArray = object.getJSONArray("result");
                    for (int i = 0; i < itemArray.length(); i++) {
                        String itemName = itemArray.getString(i);
                        PantryItemModel itemModel = new PantryItemModel(itemName);
                        itemList.add(itemModel);
                    }




                    // Create and set the adapter
                    adapter = new PantryItemListAdapter(PantryOrderPlaceActivity.this, meetId, itemList);
                    adapter.setOrderListener(PantryOrderPlaceActivity.this); // Set the order listener
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);
            }
        }).get_pantry_item(); // Call the correct API method to retrieve the pantry list
    }

    @Override
    public void onOrderListUpdated(List<OrderPlaceModel> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
