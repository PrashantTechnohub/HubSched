package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Adapters.PantryItemListAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Models.OrderPlaceModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryItemModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantryOrderActivity extends AppCompatActivity  {

    private List<PantryItemModel> itemList;
    private RecyclerView recyclerView;
    private PantryItemListAdapter adapter;
    private SwipeRefreshLayout refresh;

    ProgressDialog pd;
    List<OrderPlaceModel> selectedItems = new ArrayList<>();
    MaterialButton placeOrder;
    String meetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_order);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait ...");

        placeOrder = findViewById(R.id.place_order_button);
        recyclerView = findViewById(R.id.pantry_list_recyclerview);
        refresh = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                onOrderPlace(selectedItems);
            }
        });


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
        adapter = new PantryItemListAdapter(PantryOrderActivity.this,meetId, itemList);

        recyclerView.setAdapter(adapter);
    }


    public void onOrderPlace(List<OrderPlaceModel> selectedItems) {
        JSONArray jsonArray = new JSONArray();
        pd.show();

        for (OrderPlaceModel item : selectedItems) {
            try {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("meetId", item.getMeetId());
                jsonItem.put("item", item.getItemName());
                jsonItem.put("quantity", item.getQuantity());
                jsonItem.put("description", item.getDescription());
                jsonArray.put(jsonItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Create a request using Volley to post the JSON array
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, Constant.withToken(Constant.PANTRY_REQUEST_URL, PantryOrderActivity.this), jsonArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pd.dismiss();
                        // Handle the response
                        Toast.makeText(PantryOrderActivity.this, "Ordered done !", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        pd.dismiss();
                        Toast.makeText(PantryOrderActivity.this, "Failed to ordered !!", Toast.LENGTH_SHORT).show();

                    }
                });

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(PantryOrderActivity.this).addToRequestQueue(request);
    }
}