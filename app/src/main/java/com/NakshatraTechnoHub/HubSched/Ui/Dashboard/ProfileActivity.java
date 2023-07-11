package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityProfileBinding;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding bind;

    String base64Image;
    String _id, name, empId, position, gender, email, mobile, password, userType, profileDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);


        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bind.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchProfile();
            }
        });
        bind.logOutBtn.setOnClickListener(v -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProfileActivity.this, R.style.MaterialAlertDialog_Rounded);
            LayoutInflater inflater1 = (LayoutInflater) ProfileActivity.this.getSystemService(ProfileActivity.this.LAYOUT_INFLATER_SERVICE);
            View team = inflater1.inflate(R.layout.cl_alert, null);
            builder.setView(team);
            builder.setCancelable(false);

            Button logout = team.findViewById(R.id.yes_btn);
            Button no = team.findViewById(R.id.no_btn);
            TextView text = team.findViewById(R.id.alert_text);

            text.setText("Are you sure want to logout !!");

            AlertDialog dialog = builder.create();
            dialog.show();

            logout.setOnClickListener(v1 -> LocalPreference.LogOutUser(ProfileActivity.this, "out", dialog));

            no.setOnClickListener(v12 -> dialog.cancel());

        });

        bind.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] profileDetail = fetchProfile();
                _id = profileDetail[0];
                empId = profileDetail[1];
                name = profileDetail[2];
                gender = profileDetail[3];
                position = profileDetail[4];
                email = profileDetail[5];
                mobile = profileDetail[6];
                password = profileDetail[7];
                userType = profileDetail[8];

                String action = "";
                if (userType.equals("2")) {
                    action = "SelfAdmin";
                } else {
                    action = "selfEmployeeUpdate";
                }
                Intent intent = new Intent(ProfileActivity.this, CreateEmployeeActivity.class);
                intent.putExtra("actionType", action);
                intent.putExtra("id", _id);
                intent.putExtra("empId", empId);
                intent.putExtra("name", name);
                intent.putExtra("gender", gender);
                intent.putExtra("mobile", mobile);
                intent.putExtra("email", email);
                intent.putExtra("position", position);
                intent.putExtra("password", password);
                intent.putExtra("userType", userType);
                startActivity(intent);

            }
        });

        bind.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose Image for content view >>
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_PICK);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, imageIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");
                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(Intent.createChooser(imageIntent, "Select Profile"), 0);

            }
        });

        fetchProfile();


    }

    public String[] fetchProfile() {

        new Receiver(ProfileActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    _id = response.getString("_id");
                    profileDp = response.getString("profile");
                    name = response.getString("name");
                    name = response.getString("name");
                    empId = response.getString("empId");
                    position = response.getString("position");
                    gender = response.getString("gender");
                    email = response.getString("email");
                    mobile = response.getString("mobile");
                    password = response.getString("password");
                    userType = response.getString("userType");


                    try {
                        byte[] decodedBytes = Base64.decode(profileDp, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                        if (decodedBitmap != null) {
                            bind.userImg.setImageBitmap(decodedBitmap);
                        }
                    } catch (Exception e) {
                        Log.d("TAG", "onResponse: " + e.toString());
                    }


                    bind.empName.setText(name);
                    bind.empId.setText(empId);
                    bind.empPost.setText(position);
                    bind.empGender.setText(gender);
                    bind.empMail.setText(email);
                    bind.empMobile.setText(mobile);

                    if (bind.refresh.isRefreshing()) {
                        bind.refresh.setRefreshing(false);
                    }

                    pd.mDismiss();
                } catch (JSONException e) {
                    ErrorHandler.handleException(ProfileActivity.this, e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(ProfileActivity.this, error);
                pd.mDismiss();
            }
        }).getProfileDetail();

        String[] profileDetail = new String[9];
        profileDetail[0] = _id;
        profileDetail[1] = empId;
        profileDetail[2] = name;
        profileDetail[3] = gender;
        profileDetail[4] = position;
        profileDetail[5] = email;
        profileDetail[6] = mobile;
        profileDetail[7] = password;
        profileDetail[8] = userType;
        pd.mDismiss();

        return profileDetail;

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 0) {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));

                        // Convert Bitmap to Base64 string
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        // Use the base64Image string as needed

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ProfileActivity.this, R.style.MaterialAlertDialog_Rounded);
                    LayoutInflater inflater1 = (LayoutInflater) ProfileActivity.this.getSystemService(ProfileActivity.this.LAYOUT_INFLATER_SERVICE);
                    View view = inflater1.inflate(R.layout.cl_profile_image, null);
                    builder.setView(view);
                    builder.setCancelable(false);


                    ImageView imageView = view.findViewById(R.id.pre_img);
                    MaterialButton cancel = view.findViewById(R.id.pre_image_cancel_btn);
                    MaterialButton confirm = view.findViewById(R.id.pre_image_confirm_btn);


                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    imageView.setImageURI(selectedImageUri);

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateEmployeeByAdmin(base64Image);

                        }
                    });


                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                }
            }
        }
    }

    private void updateEmployeeByAdmin(String base64Image) {
        JSONObject updateObject = new JSONObject();
        try {
            updateObject.put("userType", userType);
            updateObject.put("_id", _id);
            updateObject.put("profile", base64Image);
            updateObject.put("empId", empId);
            updateObject.put("name", name);
            updateObject.put("gender", gender);
            updateObject.put("mobile", mobile);
            updateObject.put("email", email);
            updateObject.put("position", position);
            updateObject.put("password", password);

        } catch (JSONException e) {
            ErrorHandler.handleException(getApplicationContext(), e);
        }
        new Receiver(ProfileActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String msg = response.getString("message");
                    Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {

                    ErrorHandler.handleException(getApplicationContext(), e);

                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getApplicationContext(), error);

            }
        }).update_employee_by_admin(updateObject);

    }


}