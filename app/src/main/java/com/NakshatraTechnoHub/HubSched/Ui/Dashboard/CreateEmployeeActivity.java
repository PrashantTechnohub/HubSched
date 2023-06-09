package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.Api.VolleySingleton;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.StartActivity.LoginActivity;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateEmployeeBinding;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateMeetingBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateEmployeeActivity extends BaseActivity {

    ProgressDialog pd;

    ArrayAdapter<String> positionAdapter;
    String []positionItem = {"Organizer", "HR", "Manager", "Designer", "Tester", "Sales Manager", "Software Engineer", "Android Developer", "Web Developer", "Accountant", "Digital Marketer", "Panetry", "Scanner Device"};

    ArrayAdapter<String> genderAdapter;
    String []genderItem = {"Male", "Female"};


    ArrayAdapter<String> updateUserType;
    String []userTypeList = {"Admin", "Organizer", "Employee", "Panetry", "Scanner Device"};

    ActivityCreateEmployeeBinding bind;

    String _id,userType, action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCreateEmployeeBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        dropDownAdapter();
        action = getIntent().getStringExtra("actionType");
        userType = getIntent().getStringExtra("userType");

        _id =  String.valueOf(getIntent().getStringExtra("id"));

        if (action!=null){
            bind.actionBar.setText("Update Profile");
            bind.addEmpBtn.setText("Update Now");
            if (action.equals("update") || action.equals("selfAdmin")){
                String empId = getIntent().getStringExtra("empId");
                String name = getIntent().getStringExtra("name");
                String gender = getIntent().getStringExtra("gender");
                String email = getIntent().getStringExtra("email");
                String mobile = getIntent().getStringExtra("mobile");
                String userPosition = getIntent().getStringExtra("position");
                String password = getIntent().getStringExtra("password");

                bind.addEmpName.setText(name);
                bind.addEmpId.setText(_id);
                bind.addEmpId.setText(empId);
                bind.addEmpGender.setText(gender);
                bind.addEmpEmail.setText(email);
                bind.addEmpMobile.setText(mobile);
                bind.addEmpPosition.setText(userPosition);
                bind.addEmpPassword.setText(password);
                bind.addEmpCPassword.setText(password);

                if (userType.equals("0")){
                    bind.userType.setText("Employee");
                }
                if (userType.equals("1")){
                    bind.userType.setText("Organizer");
                }
                if (userType.equals("2")){
                    bind.userType.setText("Admin");
                }
            }
        }



        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");



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
                Toast.makeText(CreateEmployeeActivity.this, ""+name , Toast.LENGTH_SHORT).show();
                if (empId.isEmpty()){
                    bind.addEmpId.requestFocus();
                    bind.addEmpId.setError("Empty !");
                } else if (name.isEmpty()) {
                    bind.addEmpName.requestFocus();
                    bind.addEmpName.setError("Empty !");
                }else if (email.isEmpty()) {
                    bind.addEmpEmail.requestFocus();
                    bind.addEmpEmail.setError("Empty !");
                }else if (mobile.isEmpty()) {
                    bind.addEmpMobile.requestFocus();
                    bind.addEmpMobile.setError("Empty !");
                }else if (gender.isEmpty()) {
                    Toast.makeText(CreateEmployeeActivity.this, "Please select Gender", Toast.LENGTH_SHORT).show();

                }else if (position.isEmpty()) {
                    Toast.makeText(CreateEmployeeActivity.this, "Please select Position", Toast.LENGTH_SHORT).show();
                }else if (password.isEmpty()) {
                    bind.addEmpPassword.requestFocus();
                    bind.addEmpPassword.setError("Empty !");
                }else if (confirmPassword.isEmpty()) {
                    bind.addEmpCPassword.requestFocus();
                    bind.addEmpCPassword.setError("Empty !");
                }else if (!confirmPassword.equals(password)) {
                    Toast.makeText(CreateEmployeeActivity.this, "Password not match !!", Toast.LENGTH_SHORT).show();
                }else{
                    if (action!=null){
                        if (action.equals("update")){

                            String user_type = bind.userType.getText().toString();

                            if (user_type.isEmpty()) {
                                Toast.makeText(CreateEmployeeActivity.this, "Please Select User Type", Toast.LENGTH_SHORT).show();
                            }else{
                                if (user_type.equals("Employee")){
                                    user_type= "0";
                                }
                                if (user_type.equals("Organizer")){
                                    user_type= "1";
                                }
                                updateEmployee(user_type,_id, empId, name, email, mobile, gender, position, password);
                            }
                        }

                        if (action.equals("selfAdmin")){
                            updateAdmin(userType,_id, empId, name, email, mobile, gender, position, password);

                        }

                    }else{
                        String user_type = bind.userType.getText().toString();

                        if (user_type.equals("Employee")){
                            user_type = "0";
                        }
                        if (user_type.equals("Organizer")){
                            user_type = "1";
                        }
                        if (user_type.equals("Admin")){
                            user_type = "2";
                        }


                        if (user_type.equals("Scanner Device")){
                            user_type = "-1";
                        }

                        if (user_type.equals("Panetry")){
                            user_type = "-2";
                        }

                        addEmployee(empId, name, email, mobile, gender, position, password, user_type);
                    }

                }
            }
        });
    }

    private void dropDownAdapter() {
        updateUserType= new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, userTypeList);
        bind.userType.setAdapter(updateUserType);


        genderAdapter= new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, genderItem);
        bind.addEmpGender.setAdapter(genderAdapter);


        positionAdapter= new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, positionItem);
        bind.addEmpPosition. setAdapter(positionAdapter);
    }

    private void updateAdmin(String userType, String id, String empId, String name, String email, String mobile, String gender, String position, String password) {
        pd.show();
        JSONObject params = new JSONObject();
        try {
            params.put("userType", userType);
            params.put("_id",id);
            params.put("empId",empId);
            params.put("name",name);
            params.put("gender",gender);
            params.put("mobile",mobile);
            params.put("email",email);
            params.put("position",position);
            params.put("password",password);

        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT, Constant.withToken(Constant.UPDATE_PROFILE_URL, CreateEmployeeActivity.this),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg=    response.getString("message");
                    pd.dismiss();
                    Toast.makeText(CreateEmployeeActivity.this,msg, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    pd.dismiss();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    if (error.networkResponse != null){
                        if(error.networkResponse.statusCode == 500){
                            String errorString = new String(error.networkResponse.data);
                            pd.dismiss();
                            Toast.makeText(CreateEmployeeActivity.this, errorString, Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(CreateEmployeeActivity.this, "Something went wrong or have a server issues", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    pd.dismiss();
                    Log.e("CreateEMP", "onErrorResponse: ", e );
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    private void updateEmployee(String userType ,String id, String empId, String name, String email, String mobile, String gender, String position, String password) {
        pd.show();
        JSONObject params = new JSONObject();
        try {
            params.put("userType", userType);
            params.put("_id",id);
            params.put("empId",empId);
            params.put("name",name);
            params.put("gender",gender);
            params.put("mobile",mobile);
            params.put("email",email);
            params.put("position",position);
            params.put("password",password);

        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT, Constant.withToken(Constant.EMPLOYEE_UPDATE_URL, CreateEmployeeActivity.this),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg=    response.getString("message");
                    pd.dismiss();
                   finish();
                } catch (JSONException e) {
                    pd.dismiss();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if(error.networkResponse.statusCode == 500){
                        String errorString = new String(error.networkResponse.data);
                        pd.dismiss();
                        Toast.makeText(CreateEmployeeActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    pd.dismiss();
                    Log.e("CreateEMP", "onErrorResponse: ", e );
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(objectRequest);

    }


    private void addEmployee(String empId, String name, String email, String mobile, String gender, String position, String password, String userType) {

        pd.show();
        JSONObject params = new JSONObject();
        try {
            params.put("userType", userType);
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



        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CREATE_EMP_URL,getApplicationContext()),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg=    response.getString("message");
                    pd.dismiss();
                    Toast.makeText(CreateEmployeeActivity.this,msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CreateEmployeeActivity.this, errorString, Toast.LENGTH_SHORT).show();
                    }
                    Log.e("CreateEMP", "onErrorResponse: ", error );
                }catch (Exception e){
                    Log.e("CreateEMP", "onErrorResponse: ", e );

                    Toast.makeText(CreateEmployeeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });



        VolleySingleton.getInstance(this).addToRequestQueue(objectRequest);
        
    }
}