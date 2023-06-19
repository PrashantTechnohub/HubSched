package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

public class PantryOrderedListActivity extends AppCompatActivity {

    FloatingActionButton addOrderBtn;
    RecyclerView recyclerView;
    ArrayList<PantryModel> list = new ArrayList<>();

    PantryMyOrderListAdapter adapter;

    SwipeRefreshLayout refresh;
    String meetId, companyId;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_ordered_list);

        recyclerView = findViewById(R.id.order_list_recyclerview);
        addOrderBtn = findViewById(R.id.add_order_btn);
        refresh = findViewById(R.id.refresh);

        adapter = new PantryMyOrderListAdapter(PantryOrderedListActivity.this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PantryOrderedListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        Intent intent = getIntent();
        meetId = intent.getStringExtra("meetId");
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


    }


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
            list.clear(); // Clear the existing data from the global list

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObject = jsonArray.getJSONObject(i);
                String orderedBy = dataObject.getString("orderBy");
                String status = dataObject.getString("status");
                int meetId = dataObject.getInt("meetId");
                int _Id = dataObject.getInt("_id");
                String roomAddress = dataObject.getString("address");
                JSONArray itemArray = dataObject.getJSONArray("orderList");
                PantryModel model = new PantryModel(roomAddress, orderedBy, status, meetId, _Id, itemArray);
                list.add(model); // Add the new data to the global list
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed


                    if (refresh.isRefreshing()) {
                        refresh.setRefreshing(false);
                        Toast.makeText(PantryOrderedListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                    }


                    int lastIndex = list.size() - 1;
                    if (lastIndex >= 0) {
                        recyclerView.scrollToPosition(lastIndex);
                    }
                }
            });

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