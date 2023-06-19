package com.NakshatraTechnoHub.HubSched.Ui.StartActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.BaseActivity;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;
import com.NakshatraTechnoHub.HubSched.Ui.PantryDashboard.PantryActivity;
import com.NakshatraTechnoHub.HubSched.Ui.ScannerDeviceDashboard.ScannerDeviceActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityLoginBinding;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity {

    public ActivityLoginBinding bind;

    String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);


        bind.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.mShow(LoginActivity.this);
                


                String id = bind.emailId.getText().toString();
                String pwd = bind.passwordId.getText().toString();

                if (id.isEmpty()) {
                    pd.mDismiss();
                    bind.emailId.requestFocus();
                    bind.emailId.setError("Empty");

                } else if (pwd.isEmpty()) {
                    pd.mDismiss();
                    bind.passwordId.requestFocus();
                    bind.passwordId.setError("Empty");

                } else {
                    pd.hideKeyboard(LoginActivity.this);
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseToken = task.getResult();
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

    public void loginUser(String email, String password, String firebaseToken) {

        JSONObject params = new JSONObject();

        try {
            params.put("email", email);
            params.put("password", password);
            params.put("firebaseToken", firebaseToken);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(),e);
            pd.mDismiss();
        }

        new Receiver(LoginActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
                    String type = response.getString("type");
                    String userId = response.getString("_id");
                    String company_id = response.getString("company_id");

                    LocalPreference.storeUserDetail(LoginActivity.this, type, token, userId, company_id);

                    if (type.equals("admin")) {
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        pd.mDismiss();
                        finish();
                    } else if (type.equals("employee")) {
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        pd.mDismiss();
                        finish();
                    } else if (type.equals("organiser")) {
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        pd.mDismiss();
                        finish();
                    } else if (type.equals("pantry")) {
                        Intent intent = new Intent(LoginActivity.this, PantryActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        pd.mDismiss();
                        finish();
                    } else if (type.equals("scanner")) {
                        Intent intent = new Intent(LoginActivity.this, ScannerDeviceActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        pd.mDismiss();
                        finish();
                    }


                } catch (JSONException e) {
                    pd.mDismiss();
                    throw new RuntimeException(e);
                }
                pd.mDismiss();
            }

            @Override
            public void onError(VolleyError error) {
                pd.mDismiss();
                ErrorHandler.handleVolleyError(LoginActivity.this, error);
            }
        }).loginUser(params);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("userDetails", MODE_PRIVATE);

        if (preferences.contains("type")) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if (LocalPreference.getType(LoginActivity.this).equals("scanner")) {
            Intent intent = new Intent(LoginActivity.this, ScannerDeviceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (LocalPreference.getType(LoginActivity.this).equals("pantry")) {
            Intent intent = new Intent(LoginActivity.this, PantryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}