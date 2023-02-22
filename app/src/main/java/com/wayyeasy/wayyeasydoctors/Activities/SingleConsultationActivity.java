package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.url;
import static com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants.token;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.textfield.TextInputEditText;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.databinding.ActivitySingleConsultationBinding;

import java.util.ArrayList;
import java.util.List;

public class SingleConsultationActivity extends AppCompatActivity {

    ActivitySingleConsultationBinding singleConsultation;
    user_booked_response_model user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleConsultation = ActivitySingleConsultationBinding.inflate(getLayoutInflater());
        setContentView(singleConsultation.getRoot());

        user = getIntent().getParcelableExtra("user");

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
        startActivity(intent);
    }
}