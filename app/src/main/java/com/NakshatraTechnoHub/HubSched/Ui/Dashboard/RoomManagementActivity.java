package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityRoomManagementBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomManagementActivity extends AppCompatActivity {

    ProgressDialog pd ;
    ActivityRoomManagementBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRoomManagementBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        pd = new ProgressDialog(this);
        pd.setMessage("Creating Room..");

        bind.back.setOnClickListener(v -> finish());

        bind.createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createRoom();

            }
        });



    }

    private void createRoom() {
        pd.show();
        JSONObject params = new JSONObject();
        try {
            params.put("room_no", bind.createRoomNo.getText().toString());
            params.put("room_name", bind.createRoomName.getText().toString());
            params.put("seat_cap", bind.createRoomSeat.getText().toString());
            params.put("floor_no", bind.createRoomFloor.getText().toString());
            params.put("facilities", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(RoomManagementActivity.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CREATE_ROOM_URL,getApplicationContext()),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pd.dismiss();
                Toast.makeText(RoomManagementActivity.this, "Done!!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if(error.networkResponse.statusCode == 500){
                    String errorString = new String(error.networkResponse.data);
                    Toast.makeText(RoomManagementActivity.this, errorString, Toast.LENGTH_SHORT).show();
                }

                if(error.networkResponse.statusCode == 200){
                   startActivity(new Intent(getApplicationContext(), RoomListActivity.class));
                }
                Log.e("CreateRoom", "onErrorResponse: ", error );
            }
        });





        queue.add(objectRequest);
    }
}