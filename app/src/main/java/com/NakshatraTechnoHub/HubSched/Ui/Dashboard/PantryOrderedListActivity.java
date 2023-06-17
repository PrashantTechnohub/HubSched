package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Api.SocketSingleton;
import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(PantryOrderedListActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        meetId = intent.getStringExtra("meetId");
        companyId = intent.getStringExtra("companyId");

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
        SocketSingleton socketSingleton = SocketSingleton.getInstance();
        socket = socketSingleton.getSocket();

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("Socket", "Connected");
        }).on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("Socket", "Disconnected");
        }).on("pantry_list-" + companyId, args -> {
            JSONArray jsonArray = (JSONArray) args[0];
            runOnUiThread(() -> {
                parsingOrderedList(jsonArray);
            });
        }).emit("trigger_pantry", companyId);
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
                    recyclerView.setAdapter(new MyAdapter<PantryModel>(list, new MyAdapter.OnBindInterface() {
                        @Override
                        public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                            PantryModel model = list.get(position);
                            TextView room_no = holder.itemView.findViewById(R.id.room_no);
                            TextView name = holder.itemView.findViewById(R.id.name);
                            TextView orderStatus = holder.itemView.findViewById(R.id.order_status);
                            MaterialButton showOrderBtn = holder.itemView.findViewById(R.id.show_order);

                            orderStatus.setVisibility(View.VISIBLE);

                            orderStatus.setText(model.getStatus());
                            room_no.setText(model.getRoomAddress());
                            name.setText(model.getOrderedBy());

                            showOrderBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PantryOrderedListActivity.this, R.style.MaterialAlertDialog_Rounded);
                                    LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    }, R.layout.cl_orderedlist));


                    if (refresh.isRefreshing()) {
                        refresh.setRefreshing(false);
                        Toast.makeText(PantryOrderedListActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                    }
                    pd.mDismiss();

                    // Scroll to the last item in the list
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


                    pd.mDismiss();

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