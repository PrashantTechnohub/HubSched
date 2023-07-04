package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityCreateEmployeeBinding;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateEmployeeActivity extends BaseActivity {


    ArrayAdapter<String> positionAdapter;
    String[] positionItem = {"Organizer", "HR", "Manager", "Designer", "Tester", "Sales Manager", "Software Engineer", "Android Developer", "Web Developer", "Accountant", "Digital Marketer", "Pantry", "Scanner Device"};

    ArrayAdapter<String> genderAdapter;
    String[] genderItem = {"Male", "Female"};


    ArrayAdapter<String> updateUserType;
    String[] userTypeList = {"Admin", "Organizer", "Employee", "Pantry", "Scanner Device"};

    ActivityCreateEmployeeBinding bind;

    String _id, userType, action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCreateEmployeeBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        dropDownAdapter();
        Intent intent = getIntent();
        action = intent.getStringExtra("actionType");
        _id = String.valueOf(intent.getStringExtra("id"));
        userType = String.valueOf(intent.getStringExtra("userType"));

        if (action != null) {

            if (action.equals("add")) {
                bind.actionBar.setText("Create Employee");
                bind.addEmpBtn.setText("Add Employee");
            } else {
                bind.actionBar.setText("Update Profile");
                bind.addEmpBtn.setText("Update Now");
            }

            String userType = intent.getStringExtra("userType");
            String empId = intent.getStringExtra("empId");
            String name = intent.getStringExtra("name");
            String gender = intent.getStringExtra("gender");
            String email = intent.getStringExtra("email");
            String mobile = intent.getStringExtra("mobile");
            String userPosition = intent.getStringExtra("position");
            String password = intent.getStringExtra("password");

            if (userType != null) {
                if (userType.equals("0")) {
                    bind.userType.setText("Employee");
                }
                if (userType.equals("1")) {
                    bind.userType.setText("Organizer");
                }
                if (userType.equals("2")) {
                    bind.userType.setText("Admin");
                }
                if (userType.equals("-1")) {
                    bind.userType.setText("Scanner Device");
                }
                if (userType.equals("-2")) {
                    bind.userType.setText("Pantry");
                }
            }


            bind.addEmpName.setText(name);
            bind.addEmpId.setText(empId);

            bind.addEmpGender.setText(gender);
            bind.addEmpEmail.setText(email);
            bind.addEmpMobile.setText(mobile);
            bind.addEmpPosition.setText(userPosition);
            bind.addEmpPassword.setText(password);
            bind.addEmpCPassword.setText(password);
            dropDownAdapter();
            if (action.equals("selfEmployeeUpdate")) {


                bind.addEmpId.setEnabled(false);
                bind.addEmpName.setEnabled(false);
                bind.addEmpEmail.setEnabled(false);
                bind.userPositionLayout.setEnabled(false);
                bind.userTypeLayout.setEnabled(false);
                dropDownAdapter();
            }
        } else {
            finish();
            Toast.makeText(this, "Something went wrong please try again later !", Toast.LENGTH_SHORT).show();
        }


        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bind.addEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String empId_x = bind.addEmpId.getText().toString();
                String name_x = bind.addEmpName.getText().toString();
                String email_x = bind.addEmpEmail.getText().toString();
                String mobile_x = bind.addEmpMobile.getText().toString();
                String gender_x = bind.addEmpGender.getText().toString();
                String position_x = bind.addEmpPosition.getText().toString();
                String password_x = bind.addEmpPassword.getText().toString();
                String confirmPassword_x = bind.addEmpCPassword.getText().toString();
                String user_type_x = bind.userType.getText().toString();


                boolean isValid = validateInput(empId_x, name_x, email_x, mobile_x, gender_x, position_x, user_type_x, password_x, confirmPassword_x);


                if (isValid) {

                    if (user_type_x.equals("Employee")) {
                        user_type_x = "0";
                    } else if (user_type_x.equals("Organizer")) {
                        user_type_x = "1";
                    } else if (user_type_x.equals("Admin")) {
                        user_type_x = "2";
                    } else if (user_type_x.equals("Scanner Device")) {
                        user_type_x = "-1";
                    } else if (user_type_x.equals("Pantry")) {
                        user_type_x = "-2";
                    }


                    if (action != null && action.equals("update")) {
                        updateEmployee(user_type_x, _id, empId_x, name_x, email_x, mobile_x, gender_x, position_x, password_x);
                    } else if (action != null && action.equals("SelfAdmin") || action.equals("selfEmployeeUpdate")) {
                        updateAdmin(user_type_x, _id, empId_x, name_x, email_x, mobile_x, gender_x, position_x, password_x);
                    } else {
                        addEmployee(user_type_x, empId_x, name_x, email_x, mobile_x, gender_x, position_x, password_x);
                    }

                }

            }
        });
    }

    private void dropDownAdapter() {
        updateUserType = new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, userTypeList);
        bind.userType.setAdapter(updateUserType);


        genderAdapter = new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, genderItem);
        bind.addEmpGender.setAdapter(genderAdapter);


        positionAdapter = new ArrayAdapter<>(CreateEmployeeActivity.this, R.layout.cl_list_item, positionItem);
        bind.addEmpPosition.setAdapter(positionAdapter);
    }

    private void updateAdmin(String userType, String id, String empId, String name, String email, String mobile, String gender, String position, String password) {

        JSONObject selfUpdate = new JSONObject();
        try {
            selfUpdate.put("userType", userType);
            selfUpdate.put("_id", id);
            selfUpdate.put("empId", empId);
            selfUpdate.put("name", name);
            selfUpdate.put("gender", gender);
            selfUpdate.put("mobile", mobile);
            selfUpdate.put("email", email);
            selfUpdate.put("position", position);
            selfUpdate.put("password", password);

        } catch (JSONException e) {

            ErrorHandler.handleException(getApplicationContext(), e);

        }

        finalUpdate(selfUpdate);
    }


    private void updateEmployee(String user_type_x, String _id, String empId_x, String name_x, String email_x, String mobile_x, String gender_x, String position_x, String password_x) {
        JSONObject updateObject = new JSONObject();
        try {
            updateObject.put("userType", user_type_x);
            updateObject.put("_id", _id);
            updateObject.put("empId", empId_x);
            updateObject.put("name", name_x);
            updateObject.put("gender", gender_x);
            updateObject.put("mobile", mobile_x);
            updateObject.put("email", email_x);
            updateObject.put("position", position_x);
            updateObject.put("password", password_x);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }
        finalUpdate(updateObject);
    }


    private void addEmployee(String user_type_x, String empId_x, String name_x, String email_x, String mobile_x, String gender_x, String position_x, String password_x) {


        JSONObject params = new JSONObject();
        try {
            params.put("userType", user_type_x);
            params.put("empId", empId_x);
            params.put("name", name_x);
            params.put("email", email_x);
            params.put("mobile", mobile_x);
            params.put("gender", gender_x);
            params.put("position", position_x);
            params.put("password", password_x);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }


        new Receiver(CreateEmployeeActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg = response.getString("message");

                    Toast.makeText(CreateEmployeeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {

                    ErrorHandler.handleException(getApplicationContext(), e);

                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).add_emp(params);


    }


    private void finalUpdate(JSONObject params) {
        new Receiver(CreateEmployeeActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg = response.getString("message");
                    Toast.makeText(CreateEmployeeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {

                    ErrorHandler.handleException(getApplicationContext(), e);

                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).update_profile(params);

    }


    public boolean validateInput(String empId, String name, String email, String mobile, String gender, String position, String userType, String password, String confirmPassword) {
        if (password.length() < 6) {
            Toast.makeText(this, "Password will be more than 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            // Passwords do not match
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (empId.isEmpty()) {
            bind.addEmpId.requestFocus();
            bind.addEmpId.setError("Employee ID cannot be empty");
            return false;
        }

        if (name.isEmpty()) {
            bind.addEmpName.requestFocus();
            bind.addEmpName.setError("Name cannot be empty");
            return false;
        }

        if (email.isEmpty()) {
            bind.addEmpEmail.requestFocus();
            bind.addEmpEmail.setError("Email cannot be empty");
            return false;
        } else if (!isValidEmail(email)) {
            bind.addEmpEmail.requestFocus();
            bind.addEmpEmail.setError("Invalid email format");
            return false;
        }

        if (mobile.isEmpty()) {
            bind.addEmpMobile.requestFocus();
            bind.addEmpMobile.setError("Mobile number cannot be empty");
            return false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Gender cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (position.isEmpty()) {
            Toast.makeText(this, "Position cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userType.isEmpty()) {
            Toast.makeText(this, "User cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            bind.addEmpCPassword.requestFocus();
            bind.addEmpCPassword.setError("Confirm password cannot be empty");
            return false;
        }


        return true;
    }

    private boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.+[a-z]+";
        return email.toString().matches(emailPattern);
    }


}