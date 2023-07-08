package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivitySupportBinding;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class SupportActivity extends AppCompatActivity {
    ActivitySupportBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySupportBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        bind.havingIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SupportActivity.this, R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) SupportActivity.this.getSystemService(SupportActivity.this.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_bug_report, null);
                builder.setView(team);
                builder.setCancelable(false);

                Button submit = team.findViewById(R.id.submit_query_btn);
                Button cancel = team.findViewById(R.id.cancel_btn);
                EditText query = team.findViewById(R.id.get_query_edit_text);


                AlertDialog dialog = builder.create();
                dialog.show();

                submit.setOnClickListener(v1 -> {




                    String getQuery = query.getText().toString();

                    if (getQuery.isEmpty()){
                        Toast.makeText(SupportActivity.this, "Please write issue !", Toast.LENGTH_SHORT).show();
                    }else{

                        JSONObject object = new JSONObject();
                        try {
                            object.put("description", getQuery);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        new Receiver(SupportActivity.this, new Receiver.ApiListener() {
                            @Override
                            public void onResponse(JSONObject object) {
                                try {
                                    String msg = object.getString("message");
                                    Toast.makeText(SupportActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    ErrorHandler.handleException(SupportActivity.this, e);
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                ErrorHandler.handleVolleyError(SupportActivity.this, error);
                            }
                        }).post_ticket(object);

                    }

                });

                cancel.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
            }
        });



    }
}