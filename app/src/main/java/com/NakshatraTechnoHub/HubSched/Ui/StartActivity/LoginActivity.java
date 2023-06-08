package com.NakshatraTechnoHub.HubSched.Ui.StartActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.BaseActivity;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityLoginBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity {

    ActivityLoginBinding bind;

    ProgressDialog loader;

    String firebaseToken ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);





        loader = new ProgressDialog(LoginActivity.this);
        loader.setMessage("Please wait....");


        bind.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader.show();
                
                String id = bind.emailId.getText().toString();
                String pwd = bind.passwordId.getText().toString();

                if (id.isEmpty()) {
                    loader.cancel();
                    bind.emailId.requestFocus();
                    bind.emailId.setError("Empty");

                } else if (pwd.isEmpty()) {
                    loader.cancel();
                    bind.passwordId.requestFocus();
                    bind.passwordId.setError("Empty");

                } else {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseToken  = task.getResult();
                            LocalPreference.store_FirebaseToken(LoginActivity.this, firebaseToken);
                            loginUser(id, pwd, firebaseToken);
                        } else {
                            Log.w("FCM TOKEN", "Fetching FCM registration token failed", task.getException());
                        }
                    });

                }
            }
        });

    }

    private void loginUser(String email, String password, String firebaseToken) {

        JSONObject params = new JSONObject();

        try {
            params.put("email", email);
            params.put("password", password);
            params.put("firebaseToken", firebaseToken);

        } catch (JSONException e) {
            e.printStackTrace();
            loader.cancel();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.LOGIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
                    String type = response.getString("type");
                    String id = response.getString("_id");

                    LocalPreference.storeUserDetail(LoginActivity.this,type, token, id);

                        if (type.equals("admin")) {
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            loader.cancel();
                            finish();
                        } else if (type.equals("employee")) {
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            loader.cancel();
                            finish();
                        }else if (type.equals("organiser")) {
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            loader.cancel();
                            finish();
                        }


                } catch (JSONException e) {
                    loader.cancel();
                    throw new RuntimeException(e);
                }
                loader.cancel();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.cancel();

                try {
                    if (error.networkResponse!= null){
                        if(error.networkResponse.statusCode == 500){
                            String errorString = new String(error.networkResponse.data);
                            Toast.makeText(LoginActivity.this, errorString, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(LoginActivity.this, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Log.e("CreateEMP", "onErrorResponse: ", e );
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("userDetails", MODE_PRIVATE);

        if (preferences.contains("token")){
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}