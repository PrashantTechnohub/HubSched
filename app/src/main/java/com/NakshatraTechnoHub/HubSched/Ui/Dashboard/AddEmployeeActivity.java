package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityAddEmployeeBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class AddEmployeeActivity extends AppCompatActivity {

    ProgressDialog pd;
    ArrayAdapter<String> positionAdapter;
    String []positionItem = {"Organizer", "HR", "Manager", "Designer", "Tester", "Sales Manager", "Software Engineer", "Android Developer", "Web Developer", "Accountant", "Digital Marketer"};

    ArrayAdapter<String> genderAdapter;
    String []genderItem = {"Male", "Female"};

    ActivityAddEmployeeBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);


        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");


        genderAdapter= new ArrayAdapter<>(this, R.layout.cl_list_item, genderItem);
        positionAdapter= new ArrayAdapter<>(this, R.layout.cl_list_item, positionItem);

        bind.addEmpGender.setAdapter(genderAdapter);
        bind.addEmpPosition.setAdapter(positionAdapter);

        bind.addEmpGender.setOnItemClickListener((parent, view1, position, id) -> {
            if(position==0){
            }
        });

        bind.addEmpPosition.setOnItemClickListener((parent, view12, position, id) -> {
            if(position==0){
            }
        });

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        bind.addEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String empId = bind.addEmpId.getText().toString();
                String name = bind.addEmpName.getText().toString();
                String email = bind.addEmpEmail.getText().toString();
                String mobile = bind.addEmpMobile.getText().toString();
                String gender = bind.addEmpGender.getText().toString();
                String position = bind.addEmpPosition.getText().toString();
                String password = bind.addEmpPassword.getText().toString();
                String confirmPassword = bind.addEmpCPassword.getText().toString();

                addEmployee(empId, name, email, mobile, gender, position, password, confirmPassword);

            }
        });
    }
    
    private void addEmployee(String empId, String name, String email, String mobile, String gender, String position, String password, String confirmPassword) {

        pd.show();
        JSONObject params = new JSONObject();
        try {
            params.put("empId", empId);
            params.put("name", name);
            params.put("email", email);
            params.put("mobile", mobile);
            params.put("gender", gender);
            params.put("position", position);
            params.put("password", password);
           
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(AddEmployeeActivity.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CREATE_EMP_URL,getApplicationContext()),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg=    response.getString("message");
                    pd.dismiss();
                    Toast.makeText(AddEmployeeActivity.this,msg, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                try {
                    if(error.networkResponse.statusCode == 500){
                        String errorString = new String(error.networkResponse.data);
                        Toast.makeText(AddEmployeeActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }

                    Log.e("CreateEMP", "onErrorResponse: ", error );
                }catch (Exception e){
                    Log.e("CreateEMP", "onErrorResponse: ", e );

                    Toast.makeText(AddEmployeeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });



        queue.add(objectRequest);
        
    }
}