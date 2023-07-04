package com.NakshatraTechnoHub.HubSched.Ui.Dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.NakshatraTechnoHub.HubSched.Adapters.ImageSliderAdapter;
import com.NakshatraTechnoHub.HubSched.R;
import com.NakshatraTechnoHub.HubSched.UtilHelper.Receiver;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditContentActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private List<Uri> selectedImageUris;
    private List<ImageView> imageViewsList;
    private int currentImageViewIndex;
    List<String> base64Images = new ArrayList<>();
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);


        getBanners();

        selectedImageUris = new ArrayList<>();
        imageViewsList = new ArrayList<>();
        imageViewsList.add(findViewById(R.id.image1));
        imageViewsList.add(findViewById(R.id.image2));
        imageViewsList.add(findViewById(R.id.image3));
        imageViewsList.add(findViewById(R.id.image4));
        imageViewsList.add(findViewById(R.id.image5));
        imageViewsList.add(findViewById(R.id.image6));
        imageViewsList.add(findViewById(R.id.image7));
        // Add remaining ImageViews similarly

        // Initialize the selectedImageUris list with empty Uri objects
        for (int i = 0; i < imageViewsList.size(); i++) {
            selectedImageUris.add(Uri.EMPTY);
        }

        for (ImageView imageView : imageViewsList) {
            imageView.setOnClickListener(v -> {
                int index = imageViewsList.indexOf(imageView);
                currentImageViewIndex = index;
                openImagePicker();
            });
        }

        findViewById(R.id.uploadImgBtn).setOnClickListener(view -> {
            if (isAnyImageSelected()) {
                uploadCroppedImagesToServer();
            } else {
                Toast.makeText(EditContentActivity.this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            }
        });


        findViewById(R.id.back).setOnClickListener(view -> {
          finish();
        });
    }

    private void getBanners() {
        new Receiver(EditContentActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {
                List<String> containList = new ArrayList<>();

                JSONObject bannerList = object.optJSONObject("bannerList");
                if (bannerList != null) {
                    JSONArray banners = bannerList.optJSONArray("banners");
                    if (banners != null) {
                        for (int i = 0; i < banners.length(); i++) {
                            JSONObject banner = banners.optJSONObject(i);
                            if (banner != null) {
                                String contain = banner.optString("contain");
                                containList.add(contain);
                            }
                        }
                    }
                }


                bitmapList.clear();
                bitmapList = decodeBase64ToBitmapList(containList);
                // Set bitmaps to ImageView instances
                for (int i = 0; i < bitmapList.size(); i++) {
                    if (i < imageViewsList.size()) {
                        ImageView imageView = imageViewsList.get(i);
                        Bitmap bitmap = bitmapList.get(i);
                        Glide.with(EditContentActivity.this)
                                .asBitmap()
                                .centerCrop()
                                .load(bitmap)
                                .into(new BitmapImageViewTarget(imageView));
                        base64Images.add(convertBitmapToBase64(bitmap));
                    } else {
                        // Handle case where the number of ImageView instances is less than the number of bitmaps
                    }
                }


            }

            @Override
            public void onError(VolleyError error) {
                // Handle error here
            }
        }).banner_list(new JSONObject());

    }
    private ArrayList<Bitmap> decodeBase64ToBitmapList(List<String> base64StringList) {
        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        for (String base64String : base64StringList) {
            try {
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                bitmapList.add(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return bitmapList;
    }

    private boolean isAnyImageSelected() {
        for (Uri imageUri : selectedImageUris) {
            if (!imageUri.equals(Uri.EMPTY)) {
                return true;
            }
        }
        return false;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.set(currentImageViewIndex + i, imageUri);
                        imageViewsList.get(currentImageViewIndex + i).setImageURI(imageUri);
                        startCropActivity(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    selectedImageUris.set(currentImageViewIndex, imageUri);
                    imageViewsList.get(currentImageViewIndex).setImageURI(imageUri);
                    startCropActivity(imageUri);
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri croppedImageUri = result.getUri(); // Get the cropped image URI
                    imageViewsList.get(currentImageViewIndex).setImageURI(croppedImageUri);
                    selectedImageUris.set(currentImageViewIndex, croppedImageUri); // Update the selectedImageUris with cropped image URI
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startCropActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(21, 9)
                .start(this);
    }

    private void uploadCroppedImagesToServer() {


        for (Uri imageUri : selectedImageUris) {
            if (!imageUri.equals(Uri.EMPTY)) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    String base64Image = convertBitmapToBase64(bitmap);
                    if (base64Image != null) {
                        base64Images.add(base64Image);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!base64Images.isEmpty()) {
            uploadImagesToServer(base64Images);
        }
    }



    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }




    private void uploadImagesToServer(List<String> base64Images) {
        JSONArray jsonArray = new JSONArray();

        for (String base64Image : base64Images) {
            JSONObject jsonObject = new JSONObject();
            try {
                String currentDateTime = getCurrentDateTime();
                jsonObject.put("contain", base64Image);
                jsonObject.put("dateTime", currentDateTime);
                jsonObject.put("link", "sf");
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("banners", jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        new Receiver(EditContentActivity.this, new Receiver.ApiListener() {
            @Override
            public void onResponse(JSONObject object) {

                Toast.makeText(EditContentActivity.this, "Images Successfully Uploaded", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(VolleyError error) {

            }
        }).banner_list(mainObject);
    }

    private String getCurrentDateTime() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Format the date and time as "6/27/2023 04:50 PM"
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy hh:mm a", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
