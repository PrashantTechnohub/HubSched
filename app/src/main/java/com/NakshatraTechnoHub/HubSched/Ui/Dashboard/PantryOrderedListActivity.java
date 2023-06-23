package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Adapters.PantryMyOrderListAdapter;
import com.NakshatraTechnoHub.HubSched.Api.SocketSingleton;
import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

public class PantryOrderedListActivity extends BaseActivity {

    FloatingActionButton addOrderBtn;
    RecyclerView recyclerView;
    ArrayList<PantryModel> list = new ArrayList<>();
    ArrayList<PantryModel> filteredList = new ArrayList<>();

    PantryMyOrderListAdapter adapter;

    MaterialButton acceptedBtn, requestedBtn, cancelledBtn, myOrderBtn;

    LinearLayout filterLayout;

    SwipeRefreshLayout refresh;
    String meetId, companyId;
    ImageView filterBtn;

    private int COLOR_SELECTED;
    private  int COLOR_DESELECTED;



    private String activeFilter = ""; // Stores the active filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_ordered_list);

        COLOR_SELECTED = getResources().getColor(R.color.purple_500);
        COLOR_DESELECTED = getResources().getColor(com.denzcoskun.imageslider.R.color.grey_font);

        recyclerView = findViewById(R.id.order_list_recyclerview);
        addOrderBtn = findViewById(R.id.add_order_btn);
        refresh = findViewById(R.id.refresh);
        filterLayout = findViewById(R.id.filterLayout);

        filterBtn = findViewById(R.id.filter_btn);
        acceptedBtn = findViewById(R.id.filter_accepted_btn);
        requestedBtn = findViewById(R.id.filter_requested_btn);
        myOrderBtn = findViewById(R.id.filter_my_order_btn);
        cancelledBtn = findViewById(R.id.filter_cancelled_btn);

        adapter = new PantryMyOrderListAdapter(PantryOrderedListActivity.this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PantryOrderedListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        meetId = LocalPreference.get_meetId(this);
        companyId = LocalPreference.get_company_Id(this);

        orderListApi();
        socketConnection();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderListApi();
            }
        });

        addOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantryOrderedListActivity.this, PantryOrderPlaceActivity.class);
                intent.putExtra("meetId", meetId);
                intent.putExtra("companyId", companyId);
                startActivity(intent);
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterLayout.getVisibility() == View.VISIBLE) {
                    // Hide animation
                    filterLayout.animate()
                            .alpha(0f)
                            .setDuration(500) // Adjust the duration as desired
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    filterLayout.setVisibility(View.GONE);
                                }
                            })
                            .start();
                } else {
                    // Show animation
                    filterLayout.setVisibility(View.VISIBLE);
                    filterLayout.setAlpha(0f);
                    filterLayout.animate()
                            .alpha(1f)
                            .setDuration(500) // Adjust the duration as desired
                            .start();
                }
            }
        });

        acceptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter("accepetd", acceptedBtn);
            }
        });

        requestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter("requested", requestedBtn);
            }
        });

        myOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter(LocalPreference.get_Id(PantryOrderedListActivity.this), myOrderBtn);
            }
        });

        cancelledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter("denied", cancelledBtn);
            }
        });

    }

    // Apply filter function
// Apply filter function
    private void applyFilter(String filter, MaterialButton filterButton) {
        if (activeFilter.equals(filter)) {
            // Same filter selected again, clear the active filter and set button color to gray
            activeFilter = "";
            filterButton.setBackgroundColor(COLOR_DESELECTED);
        } else {
            // Different filter selected, update the active filter and button colors
            activeFilter = filter;

            // Reset the background color of all filter buttons
            acceptedBtn.setBackgroundColor(COLOR_DESELECTED);
            requestedBtn.setBackgroundColor(COLOR_DESELECTED);
            myOrderBtn.setBackgroundColor(COLOR_DESELECTED);
            cancelledBtn.setBackgroundColor(COLOR_DESELECTED);

            // Set the background color of the selected filter button to blue
            filterButton.setBackgroundColor(COLOR_SELECTED);
        }

        adapter.filterData(activeFilter); // Apply the filter
        updateRecyclerView(); // Update the RecyclerView with the filtered data
    }

    // Update the RecyclerView with the filtered data
    private void updateRecyclerView() {
        if (activeFilter.isEmpty()) {
            // No filter applied, display the entire list
            adapter.setData(list);
        } else {
            // Filter applied, display the filtered list
            filterListByStatus(activeFilter);
            adapter.setData(filteredList);
        }

        // Scroll to the last item in the filtered list
        int lastIndex = adapter.getItemCount() - 1;
        if (lastIndex >= 0) {
            recyclerView.scrollToPosition(lastIndex);
        }
    }
    // Filter the list by status
    private void filterListByStatus(String filter) {
        filteredList.clear();
        for (PantryModel model : list) {
            if (model.getStatus().equals(filter)) {
                filteredList.add(model);
            }
        }
    }

    // Clear the filter and display the entire list
    private void clearFilter() {
        filteredList.clear();
        filteredList.addAll(list);
    }

    // Update the RecyclerView with the filtered data
    private void socketConnection() {
        Socket socket = SocketSingleton.getInstance().getSocket().connect();

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("Socket", "Connected");

            socket.emit("trigger_pantry", companyId);

        }).on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("Socket", "Disconnected");

        }).on("pantry_list-" + companyId + "-" + meetId, args -> {

            JSONArray jsonArray = (JSONArray) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parsingOrderedList(jsonArray);
                }
            });

        });
    }

    private void parsingOrderedList(JSONArray jsonArray) {
        try {
            ArrayList<PantryModel> parsedList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObject = jsonArray.getJSONObject(i);
                String orderedBy = dataObject.getString("orderBy");
                String status = dataObject.getString("status");
                int meetId = dataObject.getInt("meetId");
                int _Id = dataObject.getInt("_id");
                String roomAddress = dataObject.getString("address");
                JSONArray itemArray = dataObject.getJSONArray("orderList");
                PantryModel model = new PantryModel(roomAddress, orderedBy, status, meetId, _Id, itemArray);
                parsedList.add(model);
            }

            list = parsedList;
            adapter.setData(list);

            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
                Toast.makeText(PantryOrderedListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
                Toast.makeText(PantryOrderedListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
            ErrorHandler.handleException(getApplicationContext(), e);
        }
    }

    private void orderListApi() {
        new Receiver(PantryOrderedListActivity.this, new Receiver.ListListener() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    parsingOrderedList(jsonArray);
                    adapter.notifyDataSetChanged(); // Notify adapter about the data change

                    if (refresh.isRefreshing())
                        refresh.setRefreshing(false);

                } catch (Exception e) {
                    ErrorHandler.handleException(getApplicationContext(), e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(PantryOrderedListActivity.this, error);
            }
        }).getMyOrderList(Integer.parseInt(meetId));
    }
}
