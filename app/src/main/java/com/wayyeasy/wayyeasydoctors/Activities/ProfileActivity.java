package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.BASE_URL;
import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityProfileBinding;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding profileBinding;
    private final int UPLOAD_VERIFICATION_DOCS = 1001;
    private final int UPLOAD_PROFILE_IMAGE = 1002;
    private String docImage, profileImage;
    SharedPreferenceManager preferenceManager;
    ResponseDialog dialog;
    ProgressDialog progressDialog;

    private int hours, minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        dialog = new ResponseDialog();
        progressDialog = new ProgressDialog(ProfileActivity.this);

        updateProfileStatus();

        String[] specialitySelection = getResources().getStringArray(R.array.speciality);

        ArrayAdapter<String> specialityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, specialitySelection);
        profileBinding.specialityInput.setAdapter(specialityAdapter);

        profileBinding.editProfile.setOnClickListener(view -> {
            uploadProfileImage();
        });
    }

    private void uploadProfileImage() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ImagePicker.with(ProfileActivity.this)
                                .crop()                    //Crop image(Optional), Check Customization for more option
                                .cropSquare()
                                .compress(256)            //Final image size will be less than 1 MB(Optional)
                                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                                .start(UPLOAD_PROFILE_IMAGE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void uploadProofDoc(View view) {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ImagePicker.with(ProfileActivity.this)
                                .crop()                    //Crop image(Optional), Check Customization for more option
                                .compress(512)
                                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                                .start(UPLOAD_VERIFICATION_DOCS);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPLOAD_VERIFICATION_DOCS) {

                Uri fileUri = data.getData();
                profileBinding.proofImg.setText(data.getData().getLastPathSegment());

                try {
                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    encodeBitmapImage(bitmap, UPLOAD_VERIFICATION_DOCS);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == UPLOAD_PROFILE_IMAGE) {
                Uri fileUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    profileBinding.userProfile.setImageBitmap(bitmap);
                    encodeBitmapImage(bitmap, UPLOAD_PROFILE_IMAGE);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void encodeBitmapImage(Bitmap bitmap, int resultCode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        if (resultCode == UPLOAD_VERIFICATION_DOCS) {
            docImage = Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
        }
        if (resultCode == UPLOAD_PROFILE_IMAGE) {
            profileImage = Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
        }
    }

    public void sendDataForVerification(View view) {
        profileBinding.errorMsg.setVisibility(View.VISIBLE);
        if (profileImage == null) {
            profileBinding.errorMsg.setText("Please upload profile image.");
            return;
        }

        if (docImage == null) {
            profileBinding.errorMsg.setText("Please upload license or certificate in order to verify your account.");
            return;
        }

        profileBinding.errorMsg.setVisibility(View.GONE);
//        uploadData(profileImage, docImage, profileBinding.specialityInput.getText().toString(), profileBinding.qualificationInput.getText().toString(), profileBinding.priceInput.getText().toString(), profileBinding.shiftStartInput.getText().toString(), profileBinding.shiftEndInput.getText().toString(), profileBinding.addressInput.getText().toString(), profileBinding.descriptionInput.getText().toString(), task.getResult());
        generateFCMToken();
    }

    private void generateFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                uploadData(profileImage, docImage, profileBinding.specialityInput.getText().toString(), profileBinding.qualificationInput.getText().toString(), profileBinding.priceInput.getText().toString(), profileBinding.shiftStartInput.getText().toString(), profileBinding.shiftEndInput.getText().toString(), profileBinding.addressInput.getText().toString(), profileBinding.descriptionInput.getText().toString(), task.getResult());
            }
        });
    }

    private void uploadData(String profileImage, String docImage, String speciality, String qualification, String price, String shiftStart, String shiftEnd, String address, String desc, String token) {
        if ((speciality != null && speciality.length() > 0) && (qualification != null && qualification.length() > 0) &&
                (price != null && price.length() > 0) && (shiftStart != null && shiftStart.length() > 0) &&
                (shiftEnd != null && shiftEnd.length() > 0) && (address != null && address.length() > 0) &&
                (desc != null && desc.length() > 0)) {
            if (checkValidTime(shiftStart, shiftEnd)) {
                progressDialog.showDialog();

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put(Constants.image, profileImage);
                userMap.put(Constants.proofDocs, docImage);
                userMap.put(Constants.specialityType, speciality);
                userMap.put(Constants.qualification, qualification);
                userMap.put(Constants.price, price);
                userMap.put(Constants.shiftStart, shiftStart);
                userMap.put(Constants.shiftEnd, shiftEnd);
                userMap.put(Constants.address, address);
                userMap.put(Constants.description, desc);
                userMap.put(Constants.status, "pending");
                userMap.put(Constants.KEY_FIREBASE_USER_ID, preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID));
                userMap.put(Constants.KEY_FCM_TOKEN, token);

                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.FIREBASE_DOCTORS_DB)
                        .document(preferenceManager.getString(Constants.KEY_FIREBASE_USER_ID))
                        .update(userMap)
                        .addOnCompleteListener(task -> {
                            profileBinding.errorMsg.setVisibility(View.GONE);
                            progressDialog.dismissDialog();
                            preferenceManager.putBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN, true);
                            preferenceManager.putString(Constants.status, "pending");
                            preferenceManager.putString(Constants.image, profileImage);
                            preferenceManager.putString(Constants.proofDocs, docImage);
                            preferenceManager.putString(Constants.specialityType, speciality);
                            preferenceManager.putString(Constants.qualification, qualification);
                            preferenceManager.putString(Constants.price, price);
                            preferenceManager.putString(Constants.shiftStart, shiftStart);
                            preferenceManager.putString(Constants.shiftEnd, shiftEnd);
                            preferenceManager.putString(Constants.address, address);
                            preferenceManager.putString(Constants.description, desc);

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismissDialog();
                            dialog.showDialog(ProfileActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), e.getMessage().toString());
                        });
                
            } else {
                profileBinding.errorMsg.setVisibility(View.VISIBLE);
                profileBinding.errorMsg.setText("Selected time is not valid.");
            }
        } else {
            profileBinding.errorMsg.setVisibility(View.VISIBLE);
            profileBinding.errorMsg.setText("All fields are mandatory.");
        }
    }

    private void updateProfileStatus() {
        if (preferenceManager.getBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN)) {
            profileBinding.name.setText(preferenceManager.getString(Constants.name));
            profileBinding.email.setText(preferenceManager.getString(Constants.email));
            profileBinding.mobile.setText(preferenceManager.getString(Constants.mobile));

            if (preferenceManager.getString(Constants.status).equals("pending")) {
                profileBinding.status.setText("Pending");
                profileBinding.status.setTextColor(getResources().getColor(R.color.light_orange));
                profileBinding.specialityInput.setText(preferenceManager.getString(Constants.specialityType));
                profileBinding.qualificationInput.setText(preferenceManager.getString(Constants.qualification));
                profileBinding.priceInput.setText(preferenceManager.getString(Constants.price));
                profileBinding.shiftStartInput.setText(preferenceManager.getString(Constants.shiftStart));
                profileBinding.shiftEndInput.setText(preferenceManager.getString(Constants.shiftEnd));
                profileBinding.addressInput.setText(preferenceManager.getString(Constants.address));
                profileBinding.descriptionInput.setText(preferenceManager.getString(Constants.description));
                profileBinding.updateProfileBtn.setVisibility(View.GONE);
                profileBinding.pendingMsg.setVisibility(View.VISIBLE);
                profileBinding.pendingMsg.setText("Profile activation request has been sent.\nWe will update you within 1 day.");
            }

            if (preferenceManager.getString(Constants.status).equals("inActive")) {
                profileBinding.status.setText("In Active");
                profileBinding.status.setTextColor(getResources().getColor(R.color.red));
            }

            if (preferenceManager.getString(Constants.status).equals("active")) {
                profileBinding.updateProfileBtn.setVisibility(View.GONE);
                profileBinding.pendingMsg.setVisibility(View.GONE);
                profileBinding.pendingMsg.setText("Profile has been successfully updated");
                profileBinding.pendingMsg.setTextColor(getResources().getColor(R.color.theme_color));
                profileBinding.status.setText("Active");
                profileBinding.status.setTextColor(getResources().getColor(R.color.theme_color));
                profileBinding.name.setText(preferenceManager.getString(Constants.name));
                profileBinding.email.setText(preferenceManager.getString(Constants.email));
                profileBinding.mobile.setText(preferenceManager.getString(Constants.mobile));
                profileBinding.specialityInput.setText(preferenceManager.getString(Constants.specialityType));
                profileBinding.qualificationInput.setText(preferenceManager.getString(Constants.qualification));
                profileBinding.priceInput.setText(preferenceManager.getString(Constants.price));
                profileBinding.shiftStartInput.setText(preferenceManager.getString(Constants.shiftStart));
                profileBinding.shiftEndInput.setText(preferenceManager.getString(Constants.shiftEnd));
                profileBinding.addressInput.setText(preferenceManager.getString(Constants.address));
                profileBinding.descriptionInput.setText(preferenceManager.getString(Constants.description));
                profileBinding.proofImg.setText("Document approved");
                profileBinding.proofImg.setTextColor(getResources().getColor(R.color.theme_color));

                String imgUrl = BASE_URL + "doctors/" + preferenceManager.getString(Constants.image);

                GlideUrl glideUrl = new GlideUrl(imgUrl,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + preferenceManager.getString(Constants.token))
                                .build());

                Glide.with(ProfileActivity.this).load(glideUrl).into(profileBinding.userProfile);
            }
        }
    }

    public void selectStartTime(View view) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(ProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hours = i;
                minutes = i1;

                Calendar calendar = Calendar.getInstance();

                calendar.set(0, 0, 0, hours, minutes);

                profileBinding.shiftStartInput.setText(DateFormat.format("hh:mm aa", calendar));
            }
        }, 12, 0, false);

        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }

    public void selectEndTime(View view) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(ProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hours = i;
                minutes = i1;

                Calendar calendar = Calendar.getInstance();

                calendar.set(0, 0, 0, hours, minutes);

                profileBinding.shiftEndInput.setText(DateFormat.format("hh:mm aa", calendar));
            }
        }, 12, 0, false);

        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }

    public boolean checkValidTime(String shiftStart, String shiftEnd) {

        boolean validTime = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            Date startTime = sdf.parse(shiftStart);
            Date endTime = sdf.parse(shiftEnd);

            if (startTime.before(endTime))
                validTime = true;
            else
                validTime = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return validTime;
    }

    public void clearActivity(View view) {
        finish();
    }
}