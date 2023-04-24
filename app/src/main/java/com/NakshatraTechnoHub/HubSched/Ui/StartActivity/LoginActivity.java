package com.NakshatraTechnoHub.HubSched.Ui.StartActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.databinding.ActivityLoginBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.CheckUserPreference;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding bind;

    ProgressDialog loader;


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
                    loginUser(id, pwd);
                }
            }
        });

    }

    private void loginUser(String email, String password) {

        JSONObject params = new JSONObject();

        try {
            params.put("email", email);
            params.put("password", password);

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

                        if (type.equals("admin")) {
                            CheckUserPreference.checkTypeToken(LoginActivity.this,type, token);


                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            loader.cancel();


                            finish();
                        } else if (type.equals("employee")) {
                            CheckUserPreference.checkTypeToken(LoginActivity.this,type, token);

                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            loader.cancel();
                            finish();
                        }else if (type.equals("organiser")) {
                            CheckUserPreference.checkTypeToken(LoginActivity.this,type, token);

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
                Log.d("Login Error", "onErrorResponse: " + error.getMessage());
                loader.cancel();
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("userTypeToken", MODE_PRIVATE);

        if (preferences.contains("token")){
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}