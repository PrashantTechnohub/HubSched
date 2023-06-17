package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.databinding.ActivityEditContentBinding;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EditContentActivity extends BaseActivity {

    ActivityEditContentBinding bind;

    RequestQueue queue;

    JsonObjectRequest jsonObjectRequest;
    JSONObject object = new JSONObject();

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEditContentBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        //Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ....");

        //One Volley Queue
        queue = Volley.newRequestQueue(getApplicationContext());

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


        //Image Buttons
        bind.back.setOnClickListener(v -> finish());

        bind.contentOneImg.setOnClickListener(view1 -> startActivityForResult(Intent.createChooser(imageIntent, "Select Icon"), 0));

        bind.contentTwoImg.setOnClickListener(view12 -> startActivityForResult(Intent.createChooser(imageIntent, "Select Icon"), 1));

        bind.contentThreeImg.setOnClickListener(view12 -> startActivityForResult(Intent.createChooser(imageIntent, "Select Icon"), 2));

        bind.contentFourImg.setOnClickListener(view12 -> startActivityForResult(Intent.createChooser(imageIntent, "Select Icon"), 3));


        //Update Content Buttons
        bind.contentOneBtn.setOnClickListener(v -> {
            if(bind.contentOneName.getText().toString().isEmpty() && bind.contentOneUrl.getText().toString().isEmpty() ){
                progressDialog.dismiss();
                Toast.makeText(EditContentActivity.this, "Fill all fields !!", Toast.LENGTH_SHORT).show();
            }else{

                try {
                    object.put("url", bind.contentOneUrl.getText().toString());
//                        object.put("iconUrl", bitmapToString());
                    object.put("text", bind.contentOneName.getText().toString());
                } catch (JSONException e) {

                    throw new RuntimeException(e);
                }

            }
        });


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode ==0 || requestCode ==1 || requestCode ==2 || requestCode ==3  ) {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    if (requestCode == 0){
                        bind.contentOneImg .setImageURI(selectedImageUri);
                    }

                    if (requestCode == 1){
                        bind.contentTwoImg .setImageURI(selectedImageUri);

                    }

                    if (requestCode == 2){
                        bind.contentThreeImg .setImageURI(selectedImageUri);

                    }
                    if (requestCode == 3){
                        bind.contentFourImg .setImageURI(selectedImageUri);

                    }

                }
            }
        }
    }


}