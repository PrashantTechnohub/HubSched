package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

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
    String[] positionItem = {"Organizer", "HR", "Manager", "Designer", "Tester", "Sales Manager", "Software Engineer", "Android Developer", "Web Developer", "Accountant", "Digital Marketer", "Panetry", "Scanner Device"};

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
        action = getIntent().getStringExtra("actionType");
        userType = getIntent().getStringExtra("userType");

        _id = String.valueOf(getIntent().getStringExtra("id"));

        if (action != null) {
            bind.actionBar.setText("Update Profile");
            bind.addEmpBtn.setText("Update Now");
            if (action.equals("update") || action.equals("selfAdmin")) {
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

                if (userType.equals("0")) {
                    bind.userType.setText("Employee");
                }
                if (userType.equals("1")) {
                    bind.userType.setText("Organizer");
                }
                if (userType.equals("2")) {
                    bind.userType.setText("Admin");
                }
            }
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

                String empId = bind.addEmpId.getText().toString();
                String name = bind.addEmpName.getText().toString();
                String email = bind.addEmpEmail.getText().toString();
                String mobile = bind.addEmpMobile.getText().toString();
                String gender = bind.addEmpGender.getText().toString();
                String position = bind.addEmpPosition.getText().toString();
                String password = bind.addEmpPassword.getText().toString();
                String confirmPassword = bind.addEmpCPassword.getText().toString();
                String userType = bind.userType.getText().toString();

                boolean isValid = validateInput(empId, name, email, mobile, gender, position, userType, password, confirmPassword);
                if (isValid) {
                    if (action != null) {
                        if (action.equals("update")) {

                            String user_type = bind.userType.getText().toString();

                            if (user_type.isEmpty()) {
                                Toast.makeText(CreateEmployeeActivity.this, "Please Select User Type", Toast.LENGTH_SHORT).show();
                            } else {
                                if (user_type.equals("Employee")) {
                                    user_type = "0";
                                }
                                if (user_type.equals("Organizer")) {
                                    user_type = "1";
                                }
                                updateEmployee(user_type, _id, empId, name, email, mobile, gender, position, password);
                            }
                        }

                        if (action.equals("selfAdmin")) {
                            updateAdmin(userType, _id, empId, name, email, mobile, gender, position, password);

                        }

                    } else {
                        String user_type = bind.userType.getText().toString();

                        if (user_type.equals("Employee")) {
                            user_type = "0";
                        }
                        if (user_type.equals("Organizer")) {
                            user_type = "1";
                        }
                        if (user_type.equals("Admin")) {
                            user_type = "2";
                        }


                        if (user_type.equals("Scanner Device")) {
                            user_type = "-1";
                        }

                        if (user_type.equals("Pantry")) {
                            user_type = "-2";
                        }

                        addEmployee(empId, name, email, mobile, gender, position, password, user_type);
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
        
        JSONObject params = new JSONObject();
        try {
            params.put("userType", userType);
            params.put("_id", id);
            params.put("empId", empId);
            params.put("name", name);
            params.put("gender", gender);
            params.put("mobile", mobile);
            params.put("email", email);
            params.put("position", position);
            params.put("password", password);

        } catch (JSONException e) {

            ErrorHandler.handleException(getApplicationContext(), e);

        }
        
        finalUpdate(params);
    }

    private void updateEmployee(String userType, String id, String empId, String name, String email, String mobile, String gender, String position, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put("userType", userType);
            params.put("_id", id);
            params.put("empId", empId);
            params.put("name", name);
            params.put("gender", gender);
            params.put("mobile", mobile);
            params.put("email", email);
            params.put("position", position);
            params.put("password", password);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }
        finalUpdate(params);
    }
    
    private void addEmployee(String empId, String name, String email, String mobile, String gender, String position, String password, String userType) {

        
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
            ErrorHandler.handleException(getApplicationContext(), e);
        }


        addEmp(params);

    }

    private void addEmp(JSONObject params) {
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

        // Add more validation checks here if needed

        return true; // All validation passed
    }

    private boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.+[a-z]+";
        return email.toString().matches(emailPattern);
    }



}