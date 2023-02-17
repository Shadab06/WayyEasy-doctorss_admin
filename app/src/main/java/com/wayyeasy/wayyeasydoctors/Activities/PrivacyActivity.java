package com.wayyeasy.wayyeasydoctors.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wayyeasy.wayyeasydoctors.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding privacyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privacyBinding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(privacyBinding.getRoot());

        String path = getIntent().getStringExtra("policiesLink");

        privacyBinding.privacyView.loadUrl(path);
    }

    public void clearActivity(View view) {
        finish();
    }
}