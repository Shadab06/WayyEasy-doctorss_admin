package com.wayyeasy.wayyeasydoctors.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wayyeasy.wayyeasydoctors.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity {

    ActivityNotificationsBinding notificationsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationsBinding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(notificationsBinding.getRoot());
    }

    public void clearActivity(View view) {
        finish();
    }
}