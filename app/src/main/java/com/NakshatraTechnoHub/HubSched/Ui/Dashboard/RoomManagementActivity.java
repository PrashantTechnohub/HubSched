package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.NakshatraTechnoHub.HubSched.Api.Constant;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.databinding.ActivityRoomManagementBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoomManagementActivity extends BaseActivity {

    ProgressDialog pd ;
    ActivityRoomManagementBinding bind;
    Uri selectedImageUri;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Rooms Photos");

    String result= "";
    boolean[] selectedFacilities;
    ArrayList<Integer> facilitiesList = new ArrayList<>();
    String[] facilitiesArray = {"Whiteboard/Flip-chart", "Wi-Fi/internet", "Air conditioning/heating", "Refreshments", "Power outlets" , "Restrooms", "Projector"};

    JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRoomManagementBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);
        pd = new ProgressDialog(this);
        pd.setMessage("Creating Room..");

        
        selectedFacilities = new boolean[facilitiesArray.length];



        bind.back.setOnClickListener(v -> finish());

        bind.createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();

            }
        });

        bind.selectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_PICK);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, imageIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");
                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(Intent.createChooser(imageIntent, "Select Images"), 0);
            }
        });


        bind.createRoomFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomManagementActivity.this);

                // set title
                builder.setTitle("Select Facilities");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(facilitiesArray, selectedFacilities, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            facilitiesList.add(i);
                            // Sort array list
                            Collections.sort(facilitiesList);
                        } else {
                            // when checkbox unselected
                            // Remove position from facilitiesList
                            facilitiesList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        jsonArray = new JSONArray();

                        result = "";

                        for (int j = 0; j < facilitiesList.size(); j++) {
                            String op = facilitiesArray[facilitiesList.get(j)];
                            if (j != facilitiesList.size() - 1) {
                                jsonArray.put(op);
                                result = result+(j+1)+"."+op+"\n";
                            }
                        }
                        // set text on textView
                       bind.createRoomFacilities.setText(result);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedFacilities.length; j++) {
                            selectedFacilities[j] = false;
                            facilitiesList.clear();
                            bind.createRoomFacilities.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            
            }
        });
    }


    private String getFileExtension(Uri selectedImageUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(selectedImageUri));
    }

    public static String generateRandomImageName() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int LENGTH = 10;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString() ; // you can change the file extension according to your requirement
    }

    private void createRoom() {
        pd.show();

        if (selectedImageUri!=null){
            StorageReference fileReference = storageReference.child( generateRandomImageName()+"." +getFileExtension(selectedImageUri));
            fileReference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            saveRoomDetail(String.valueOf(downloadUri));
                            bind.imge.setImageURI(downloadUri);
                            Toast.makeText(RoomManagementActivity.this, String.valueOf(downloadUri), Toast.LENGTH_SHORT).show();

                        }
                    });


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(RoomManagementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            saveRoomDetail(String.valueOf("https://www.industriousoffice.com/wp-content/uploads/2021/04/Hero-WillisTower.jpg"));

        }


    }

    private void saveRoomDetail(String imageUrl) {
        JSONObject params = new JSONObject();
        try {
            params.put("room_no", bind.createRoomNo.getText().toString());
            params.put("room_name", bind.createRoomName.getText().toString());
            params.put("seat_cap", bind.createRoomSeat.getText().toString());
//            params.put("room_image", imageUrl);
            params.put("floor_no", bind.createRoomFloor.getText().toString());
            params.put("facilities",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(RoomManagementActivity.this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Constant.withToken(Constant.CREATE_ROOM_URL,getApplicationContext()),params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pd.dismiss();
                Toast.makeText(RoomManagementActivity.this, "Done!!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                if(error.networkResponse.statusCode == 500){
                    String errorString = new String(error.networkResponse.data);
                    Toast.makeText(RoomManagementActivity.this, errorString, Toast.LENGTH_SHORT).show();
                }

                if(error.networkResponse.statusCode == 200){
                    startActivity(new Intent(getApplicationContext(), RoomListActivity.class));
                }
                Log.e("CreateRoom", "onErrorResponse: ", error );
            }
        });

        queue.add(objectRequest);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode ==0  ) {

                selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    bind.img.setImageURI(selectedImageUri);

                }
            }
        }

    }

}