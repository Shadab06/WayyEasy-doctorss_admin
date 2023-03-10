package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.BASE_URL;
import static com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants.token;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.single_user_booked;
import com.wayyeasy.wayyeasydoctors.Models.single_user_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivitySingleConsultationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleConsultationActivity extends AppCompatActivity {

    ActivitySingleConsultationBinding singleConsultation;

    public static final String TAG = "SingleConsultation";

    single_user_booked user;

    single_user_response_model fetchedUser;

    SharedPreferenceManager preferenceManager;
    ProgressDialog progressDialog;
    ResponseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleConsultation = ActivitySingleConsultationBinding.inflate(getLayoutInflater());
        setContentView(singleConsultation.getRoot());

        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        progressDialog = new ProgressDialog(SingleConsultationActivity.this);
        dialog = new ResponseDialog();

        user = getIntent().getParcelableExtra("user");

        fetchThisUser(user.getUserId());

        if (user != null) {
            singleConsultation.userName.setText(user.getName());
            singleConsultation.userAge.setText(user.getAge());
            singleConsultation.consultation.setText(user.getConsultation());

            String imgUrl = BASE_URL + "doctors/" + user.getProfileImage();

            GlideUrl glideUrl = new GlideUrl(imgUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());

            Glide.with(this).load(glideUrl).into(singleConsultation.userProfile);
        }
    }

    public void clearActivity(View view) {
        finish();
    }

    public void makeAudioCall(View view) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitation.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    public void makeVideoCall(View view) {
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitation.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    public void addPrescription(View view) {
        Intent intent = new Intent(getApplicationContext(), PrescriptionActivity.class);
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("prescription", fetchedUser.getPrescription());
        startActivity(intent);
    }

    private void fetchThisUser(String userId) {
        progressDialog.showDialog();
        Call<single_user_response_model> call = ApiControllers.getInstance()
                .getApi()
                .fetchUser("Bearer " + preferenceManager.getString(Constants.token), userId);

        call.enqueue(new Callback<single_user_response_model>() {
            @Override
            public void onResponse(Call<single_user_response_model> call, Response<single_user_response_model> response) {
                progressDialog.dismissDialog();
                fetchedUser = response.body();

                if (fetchedUser != null) {
                    singleConsultation.consultation.setText(fetchedUser.getAmountPaid());

                }
            }

            @Override
            public void onFailure(Call<single_user_response_model> call, Throwable t) {
                progressDialog.dismissDialog();
                Log.d(TAG, "onFailure: 219 " + t.getMessage());
                dialog.showDialog(SingleConsultationActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), t.getMessage());
            }
        });
    }

    public void closeConsultation(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Alert");
        builder1.setIcon(R.drawable.ic_found);
        builder1.setMessage("Are you sure you want to finish this consultation?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog1, id) -> {
                    progressDialog.showDialog();
                    Call<verify_response_model> call = ApiControllers.getInstance()
                            .getApi()
                            .finishConsultation("Bearer " + preferenceManager.getString(Constants.token), user.getUserId());

                    call.enqueue(new Callback<verify_response_model>() {
                        @Override
                        public void onResponse(Call<verify_response_model> call, Response<verify_response_model> response) {

                            dialog1.dismiss();
                            progressDialog.dismissDialog();

                            if (response.body().getMessage() != null && response.body().getMessage().length() > 0) {
                                dialog.showDialog(SingleConsultationActivity.this, getResources().getDrawable(R.drawable.ic_success), "Successful", getResources().getColor(R.color.green), response.body().getMessage());
                            } else  {
                                dialog.showDialog(SingleConsultationActivity.this, getResources().getDrawable(R.drawable.ic_found), "Error", getResources().getColor(R.color.light_orange), response.body().getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<verify_response_model> call, Throwable t) {
                            dialog1.dismiss();
                            progressDialog.dismissDialog();
                            Log.d(TAG, "onFailure: 219 " + t.getMessage());
                            dialog.showDialog(SingleConsultationActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), t.getMessage());
                        }
                    });
                });

        builder1.setNegativeButton(
                "Cancel",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}