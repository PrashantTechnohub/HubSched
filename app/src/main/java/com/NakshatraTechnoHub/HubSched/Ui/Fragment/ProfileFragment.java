package com.NakshatraTechnoHub.HubSched.Ui.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.databinding.FragmentProfileBinding;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding bind;

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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_Rounded);
                LayoutInflater inflater1 = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View team = inflater1.inflate(R.layout.cl_edit_profile, null);
                builder.setView(team);
                builder.setCancelable(true);
                final AlertDialog dialog = builder.create();
                dialog.show();

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
    private void fetchProfile() {

      RequestQueue  requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Constant.withToken(Constant.EMP_PROFILE_URL, requireContext()), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    String empId = response.getString("empId");
                    String position = response.getString("position");
                    String gender = response.getString("gender");
                    String email = response.getString("email");
                    String mobile = response.getString("mobile");

                    bind.empName.setText(name);
                    bind.empId.setText(empId);
                    bind.empPost.setText(position);
                    bind.empGender.setText(gender);
                    bind.empMail.setText(email);
                    bind.empMobile.setText(mobile);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(objectRequest);

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



}