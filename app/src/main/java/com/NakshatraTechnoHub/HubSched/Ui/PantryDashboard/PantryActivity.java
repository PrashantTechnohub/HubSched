package com.NakshatraTechnoHub.HubSched.Ui.PantryDashboard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.Adapters.PantryAdapter;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.SocketSingleton;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Interface.ApiInterface;
import com.NakshatraTechnoHub.HubSched.Models.ItemModel;
import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.MyAdapter;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import pl.droidsonroids.gif.GifImageView;

public class PantryActivity extends AppCompatActivity implements ApiInterface {
    RecyclerView recyclerView;

    FloatingActionButton addItemBtn;
    ArrayList<PantryModel> list = new ArrayList<>();
    PantryAdapter adapter;

    SwipeRefreshLayout refresh;
    ImageView logoutBtn;
    String companyId;
    MaterialCardView pd;
    GifImageView empty;
    ArrayList<ItemModel> itemList = new ArrayList<>();
    MyAdapter<ItemModel> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        recyclerView = findViewById(R.id.get_order_recyclerview);
        addItemBtn = findViewById(R.id.addItem_btn);
        logoutBtn = findViewById(R.id.logoutBtn);
        refresh = findViewById(R.id.refresh);
        companyId = LocalPreference.get_company_Id(PantryActivity.this);

        pd = findViewById(R.id.pd);
        empty = findViewById(R.id.no_result);

        pd.setVisibility(View.VISIBLE);


        getData();
        socketConnection();


        adapter = new PantryAdapter(PantryActivity.this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PantryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        PantryAdapter.setHandler(this);


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                getData();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PantryActivity.this, R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_alert, null);
                builder.setView(team);
                builder.setCancelable(false);

                Button logout = team.findViewById(R.id.yes_btn);
                Button no = team.findViewById(R.id.no_btn);
                TextView text = team.findViewById(R.id.alert_text);

                text.setText("Are you sure want to logout !!");

                AlertDialog dialog = builder.create();
                dialog.show();

                logout.setOnClickListener(v1 -> LocalPreference.LogOutUser(PantryActivity.this, "outPantry", dialog));

                no.setOnClickListener(v12 -> dialog.cancel());

            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PantryActivity.this, R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_add_pantry_item, null);
                builder.setView(team);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();

                ImageView closeDialog = team.findViewById(R.id.close_dialog);
                MaterialButton addItem = team.findViewById(R.id.add_pantry_item_btn);
                EditText getItem_EditText = team.findViewById(R.id.item_edittext);
                MaterialButton saveItem = team.findViewById(R.id.save_pantry_item_btn);
                RecyclerView pantryItemRecycler = team.findViewById(R.id.pantryItemRecycler);


                itemAdapter = new MyAdapter<ItemModel>(itemList, new MyAdapter.OnBindInterface() {
                    @Override
                    public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                        ItemModel model = itemList.get(position);
                        TextView item = holder.itemView.findViewById(R.id.pantry_item_name);
                        TextView sn = holder.itemView.findViewById(R.id.item_c);
                        ImageView delete = holder.itemView.findViewById(R.id.delete_item);
                        item.setText(model.getItem());
                        sn.setText((position + 1) + "");

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Handle delete item action
                                itemList.remove(position); // Remove the item from the list
                                if (itemAdapter != null) {
                                    itemAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                                }
                            }
                        });
                    }
                }, R.layout.cl_pantry_item_list);

                pantryItemRecycler.setLayoutManager(new LinearLayoutManager(PantryActivity.this));
                pantryItemRecycler.setAdapter(itemAdapter);

                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String itemName = getItem_EditText.getText().toString().trim();
                        if (!itemName.isEmpty()) {
                            ItemModel model = new ItemModel(itemName.toUpperCase());
                            itemList.add(model);
                            int newPosition = itemList.size() - 1; // Index of the newly added item
                            itemAdapter.notifyDataSetChanged();
                            getItem_EditText.setText(""); // Clear the text after adding the item
                            pantryItemRecycler.scrollToPosition(newPosition); // Scroll to the newly added item
                        }
                    }
                });

                saveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postPantryItem(itemList);
                    }
                });

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            private void postPantryItem(ArrayList<ItemModel> itemList) {
                // Convert itemList to a list of strings
                ArrayList<String> itemStrings = new ArrayList<>();
                for (ItemModel item : itemList) {
                    itemStrings.add(item.getItem());
                }

                // Create a JSON object with the required parameters
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("items", new JSONArray(itemStrings));
                    // Add any other required parameters to the requestBody if needed
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Receiver(PantryActivity.this, new Receiver.ApiListener() {
                    @Override
                    public void onResponse(JSONObject object) {

                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                }).set_pantry_item(requestBody);
            }
        });

    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PantryActivity.this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_alert, null);
        builder.setView(team);
        builder.setCancelable(false);

        Button yes = team.findViewById(R.id.yes_btn);
        Button no = team.findViewById(R.id.no_btn);
        TextView text = team.findViewById(R.id.alert_text);

        text.setText("Are you sure you want to exit?");
        AlertDialog dialog = builder.create();
        dialog.show();

        yes.setOnClickListener(v1 -> {
            finish();
            dialog.cancel();
            clearFromRecentTasks();
        });

        no.setOnClickListener(v12 -> dialog.cancel());
    }

    private void clearFromRecentTasks() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.AppTask appTask = getSystemService(ActivityManager.class).getAppTasks().get(0);
            appTask.finishAndRemoveTask();
        } else {
            // For older Android versions
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(getPackageName());
        }
    }

    private void getData() {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.TRIGGER_PANTRY + "/" + companyId, PantryActivity.this), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null) {
                    parsingOrderedList(response);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    pd.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.setVisibility(View.GONE);
                pd.setVisibility(View.GONE);
                refresh.setRefreshing(false);
                empty.setVisibility(View.VISIBLE);

                ErrorHandler.handleException(PantryActivity.this, error);


            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
    }

    private void socketConnection() {

        Socket socket = SocketSingleton.getInstance().getSocket().connect();

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("Socket", "Connected");

        }).on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("Socket", "Disconnected");

        }).on("pantry_list-" + companyId, args -> {

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
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
                Toast.makeText(PantryActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
            if (jsonArray != null && jsonArray.length() > 0) {
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
                        pd.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                        if (refresh.isRefreshing()) {
                            refresh.setRefreshing(false);
                            Toast.makeText(PantryActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                        }
                        int lastIndex = list.size() - 1;
                        if (lastIndex >= 0) {
                            recyclerView.scrollToPosition(lastIndex);
                        }
                    }
                });
            } else {

                pd.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


        } catch (JSONException e) {

            ErrorHandler.handleException(getApplicationContext(), e);
        }
    }


    @Override
    public void clickHandler(int id, Boolean b, int pos) {
        JSONObject params = new JSONObject();

        try {
            params.put("pantry_id", id);
            params.put("accepted", b);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.PANTRY_RESPOND_REQUEST_URL, PantryActivity.this), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String msg = response.getString("message");
                    Toast.makeText(PantryActivity.this, msg, Toast.LENGTH_SHORT).show();
                    getData();

                } catch (JSONException e) {
                    ErrorHandler.handleException(getApplicationContext(), e);

                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);


            }
        });


        VolleySingleton.getInstance(PantryActivity.this).addToRequestQueue(request);
    }
}