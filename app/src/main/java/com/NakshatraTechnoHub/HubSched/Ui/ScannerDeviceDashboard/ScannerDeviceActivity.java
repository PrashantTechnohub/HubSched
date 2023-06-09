package com.NakshatraTechnoHub.HubSched.Ui.ScannerDeviceDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.DashboardActivity;
import com.NakshatraTechnoHub.HubSched.Ui.PanetryDashboard.PanetryActivity;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class ScannerDeviceActivity extends AppCompatActivity {
    AlertDialog dialog;
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_device);
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ScannerDeviceActivity.this, R.style.MaterialAlertDialog_Rounded);
        LayoutInflater inflater1 = (LayoutInflater) ScannerDeviceActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View team = inflater1.inflate(R.layout.cl_generate_qr_code, null);
        builder.setView(team);
        builder.setCancelable(false);

        Button submit = team.findViewById(R.id.submit_btn);
        EditText meetId = team.findViewById(R.id.editText_meetId);
        dialog = builder.create();
        dialog.show();
        
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = meetId.getText().toString();
                if (id.isEmpty()){
                    Toast.makeText(ScannerDeviceActivity.this, "Please enter meet id !!", Toast.LENGTH_SHORT).show();
                }else{
                    startScanning(id);
                }
            }
        });

      
    }

    private void startScanning(String meetId) {
        dialog.dismiss();
        scannerView.setVisibility(View.VISIBLE);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(result.getText());
                            String code = jsonObject.getString("code");
                            String userId = jsonObject.getString("userId");

                            JSONObject params = new JSONObject();

                            try {
                                params.put("code", code);
                                params.put("userId", userId);
                                params.put("meetId", meetId);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.ATTENDENCE_TEST_URL, ScannerDeviceActivity.this), params, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String message = response.getString("message");
                                        Toast.makeText(ScannerDeviceActivity.this, message, Toast.LENGTH_SHORT).show();
                                        scannerView.setVisibility(View.GONE);

                                        dialog.show();
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }


                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    try {
                                        if (error.networkResponse!= null){
                                            if(error.networkResponse.statusCode == 500){
                                                String errorString = new String(error.networkResponse.data);
                                                Toast.makeText(ScannerDeviceActivity.this, errorString, Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(ScannerDeviceActivity.this, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                                        }

                                    }catch (Exception e){
                                        Log.e("CreateEMP", "onErrorResponse: ", e );
                                        Toast.makeText(ScannerDeviceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            VolleySingleton.getInstance(ScannerDeviceActivity.this).addToRequestQueue(request);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}