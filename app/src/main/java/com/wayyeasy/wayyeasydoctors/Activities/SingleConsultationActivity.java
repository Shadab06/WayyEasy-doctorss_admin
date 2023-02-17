package com.wayyeasy.wayyeasydoctors.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.wayyeasy.wayyeasydoctors.databinding.ActivitySingleConsultationBinding;

public class SingleConsultationActivity extends AppCompatActivity {

    ActivitySingleConsultationBinding singleConsultation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleConsultation = ActivitySingleConsultationBinding.inflate(getLayoutInflater());
        setContentView(singleConsultation.getRoot());
    }
}