package com.wayyeasy.wayyeasydoctors.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wayyeasy.wayyeasydoctors.databinding.ActivityEarningsBinding;

public class EarningsActivity extends AppCompatActivity {

    ActivityEarningsBinding earningsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        earningsBinding = ActivityEarningsBinding.inflate(getLayoutInflater());
        setContentView(earningsBinding.getRoot());
    }

    public void clearActivity(View view) {
        finish();
    }
}