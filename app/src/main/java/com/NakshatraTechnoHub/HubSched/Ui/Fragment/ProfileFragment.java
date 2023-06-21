package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.Ui.Dashboard.CreateEmployeeActivity;
import com.NakshatraTechnoHub.HubSched.UtilHelper.ErrorHandler;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.NakshatraTechnoHub.HubSched.UtilHelper.pd;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentProfileBinding;
import com.android.volley.VolleyError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding bind;

    String _id, name,empId,position,gender,email,mobile, password, userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bind = FragmentProfileBinding.inflate(inflater);
        MaterialToolbar toolbar = getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Account");
        

        bind.logOutBtn.setOnClickListener(v -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MaterialAlertDialog_Rounded);
            LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View team = inflater1.inflate(R.layout.cl_alert, null);
            builder.setView(team);
            builder.setCancelable(false);

            Button logout = team.findViewById(R.id.yes_btn);
            Button no = team.findViewById(R.id.no_btn);
            TextView text = team.findViewById(R.id.alert_text);

            text.setText("Are you sure want to logout !!");

            AlertDialog dialog = builder.create();
            dialog.show();

            logout.setOnClickListener(v1 -> LocalPreference.LogOutUser(getActivity(), "out", dialog));

            no.setOnClickListener(v12 -> dialog.cancel());

        });

        bind.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]profileDetail = fetchProfile();
                _id = profileDetail[0];
                empId = profileDetail[1];
                name = profileDetail[2];
                gender = profileDetail[3];
                position = profileDetail[4];
                email = profileDetail[5];
                mobile = profileDetail[6];
                password = profileDetail[7];
                userType = profileDetail[8];

                Intent intent = new Intent(requireContext(), CreateEmployeeActivity.class);
                intent.putExtra("actionType","selfAdmin");
                intent.putExtra("id",_id);
                intent.putExtra("empId",empId);
                intent.putExtra("name",name);
                intent.putExtra("gender",gender);
                intent.putExtra("mobile",mobile);
                intent.putExtra("email",email);
                intent.putExtra("position",position);
                intent.putExtra("password",password);
                intent.putExtra("userType",userType);
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

        return bind.getRoot();
    }
    private String[] fetchProfile() {
        
        new Receiver(getContext(), new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    _id = response.getString("_id");
                    name = response.getString("name");
                    empId = response.getString("empId");
                    position = response.getString("position");
                    gender = response.getString("gender");
                    email = response.getString("email");
                    mobile = response.getString("mobile");
                    password = response.getString("password");
                    userType = response.getString("userType");

                    bind.empName.setText(name);
                    bind.empId.setText(empId);
                    bind.empPost.setText(position);
                    bind.empGender.setText(gender);
                    bind.empMail.setText(email);
                    bind.empMobile.setText(mobile);



                } catch (JSONException e) {
                    ErrorHandler.handleException(getActivity(), e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ErrorHandler.handleVolleyError(getActivity(), error);
                pd.mDismiss();
            }
        }).getProfileDetail();




        String[]profileDetail = new String[9];
        profileDetail[0] =_id;
        profileDetail[1] =empId;
        profileDetail[2] =name;
        profileDetail[3] =gender;
        profileDetail[4] =position;
        profileDetail[5] =email;
        profileDetail[6] =mobile;
        profileDetail[7] =password;
        profileDetail[8] =userType;
        pd.mDismiss();
        return profileDetail;

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode ==0  ) {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) { 

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_Rounded);
                    LayoutInflater inflater1 = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater1.inflate(R.layout.cl_profile_image, null);
                    builder.setView(view);
                    builder.setCancelable(false);


                    ImageView imageView = view.findViewById(R.id.pre_img);
                    MaterialButton cancel = view.findViewById(R.id.pre_image_cancel_btn);


                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    imageView.setImageURI(selectedImageUri);

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


    @Override
    public void onStart() {
        super.onStart();
        fetchProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfile();

    }
}