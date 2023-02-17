package com.wayyeasy.wayyeasydoctors.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wayyeasy.wayyeasydoctors.Fragments.LoginFragment;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        getSupportFragmentManager().beginTransaction().add(R.id.frame, new LoginFragment()).commit();
    }
}