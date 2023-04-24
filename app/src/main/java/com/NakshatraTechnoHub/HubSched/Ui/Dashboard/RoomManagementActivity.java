package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
        pd.setTitle("Creating>..");

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
            params.put("room_id", bind.createRoomId.getText().toString());
            params.put("room_name", bind.createRoomName.getText().toString());
            params.put("seat", bind.createRoomSeat.getText().toString());
            params.put("floor", bind.createRoomFloor.getText().toString());
            params.put("facilities", "bind.createRoomFacilities.getText().toString()");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(RoomManagementActivity.this);

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.POST, Constant.CREATE_ROOM_URL,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pd.dismiss();
                Toast.makeText(RoomManagementActivity.this, "Done!!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("CreateRoom", "onErrorResponse: ", error );
            }
        });



        queue.add(arrayRequest);
    }
}