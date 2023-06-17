package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Adapters.PantryItemListAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.OrderPlaceModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryItemModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantryOrderPlaceActivity extends AppCompatActivity implements PantryItemListAdapter.OrderListener  {

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

        pd.mShow(this);
        placeOrder = findViewById(R.id.place_order_button);
        recyclerView = findViewById(R.id.pantry_list_recyclerview);
        refresh = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent() ;
        meetId = intent.getStringExtra("meetId");


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItemList();
            }
        });
        getItemList();

        selectedItems = adapter.getSelectedItems();

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<OrderPlaceModel> selectedItems = adapter.getSelectedItems();
                showSelectedItemsDialog(selectedItems);
            }
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

        builder.setPositiveButton("Place Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postSelectedItems(selectedItems);
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void postSelectedItems(List<OrderPlaceModel> selectedItems) {
        // Create a JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("meetId", meetId);

            // Create a JSON array for the items
            JSONArray itemsArray = new JSONArray();
            for (OrderPlaceModel item : selectedItems) {
                JSONObject itemObject = new JSONObject();
//
                itemObject.put("itemName", item.getItemName());
                itemObject.put("quantity", item.getQuantity());
                itemsArray.put(itemObject);
            }
            requestBody.put("OrderedList", itemsArray);
        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }

        // Create a Volley request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.PANTRY_REQUEST_URL, PantryOrderPlaceActivity.this), requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(PantryOrderPlaceActivity.this, "Ordered Successful", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorHandler.handleVolleyError(getApplicationContext(), error);
                        pd.mDismiss();
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);


    }


    private void getItemList() {
        itemList = new ArrayList<>();
        Intent intent = getIntent() ;
        meetId = intent.getStringExtra("meetId");

        // Add pantry items to the list
        List<String> itemNames = Arrays.asList("TEA", "WATER", "PEPSI", "SAMOSA", "JUICE");
        for (String itemName : itemNames) {
            PantryItemModel itemModel = new PantryItemModel(itemName);
            itemList.add(itemModel);
            if (refresh.isRefreshing()){
                refresh.setRefreshing(false);
                Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
        }

        // Create and set the adapter
        adapter = new PantryItemListAdapter(PantryOrderPlaceActivity.this,meetId, itemList);
        recyclerView.setAdapter(adapter);
        pd.mDismiss();
    }



    @Override
    public void onOrderListUpdated(List<OrderPlaceModel> selectedItems) {

    }
}