package com.NakshatraTechnoHub.HubSched.Ui.PantryDashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.NakshatraTechnoHub.HubSched.Models.PantryModel;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

public class PantryActivity extends AppCompatActivity implements ApiInterface {
    RecyclerView recyclerView;

    FloatingActionButton addItemBtn;
    ArrayList<PantryModel> list = new ArrayList<>();
    PantryAdapter adapter;

    SwipeRefreshLayout refresh;
    ImageView logoutBtn;
    String companyId;

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        pd.mShow(this);

        recyclerView = findViewById(R.id.get_order_recyclerview);
        addItemBtn = findViewById(R.id.addItem_btn);
        logoutBtn = findViewById(R.id.logoutBtn);
        refresh = findViewById(R.id.refresh);
        companyId = LocalPreference.get_company_Id(PantryActivity.this);

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

                Toast.makeText(PantryActivity.this, companyId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getData() {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.withToken(Constant.TRIGGER_PANTRY + "/" + companyId, PantryActivity.this), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                parsingOrderedList(response);
                pd.mDismiss();
                if (refresh.isRefreshing()) {
                    refresh.setRefreshing(false);
                    Toast.makeText(PantryActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.mDismiss();
                refresh.setRefreshing(false);
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
                        Toast.makeText(PantryActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
                    }
                    pd.mDismiss();
                    int lastIndex = list.size() - 1;
                    if (lastIndex >= 0) {
                        recyclerView.scrollToPosition(lastIndex);
                    }
                }
            });
        } catch (JSONException e) {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
                Toast.makeText(PantryActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            }
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