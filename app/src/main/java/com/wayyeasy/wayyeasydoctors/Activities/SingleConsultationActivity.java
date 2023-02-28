package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.url;
import static com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants.token;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.textfield.TextInputEditText;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.Models.single_user_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivitySingleConsultationBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleConsultationActivity extends AppCompatActivity {

    ActivitySingleConsultationBinding singleConsultation;

    public static final String TAG = "SingleConsultation";

    user_booked_response_model user;

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

            String imgUrl = url + "doctors/" + user.getProfileImage();

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
}