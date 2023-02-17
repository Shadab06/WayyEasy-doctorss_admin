package com.wayyeasy.wayyeasydoctors.Fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.wayyeasy.wayyeasydoctors.Activities.DashboardActivity;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.FragmentRegisterBinding;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    FragmentRegisterBinding registerBinding;
    ResponseDialog dialog;
    ProgressDialog progressDialog;
    SharedPreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        registerBinding.registerHeading.setPaintFlags(registerBinding.registerHeading.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        dialog = new ResponseDialog();
        progressDialog = new ProgressDialog(getActivity());
        preferenceManager = new SharedPreferenceManager(getActivity());

        registerBinding.registerBtn.setOnClickListener(view ->
                registerUser(registerBinding.name.getText().toString().trim(), registerBinding.email.getText().toString().trim(),
                registerBinding.mobile.getText().toString().trim(), registerBinding.password.getText().toString().trim(),
                registerBinding.cPassword.getText().toString().trim()));

        registerBinding.cPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (registerBinding.password.getText().toString().trim().equals(charSequence.toString().trim()))
                    registerBinding.errorMsg.setVisibility(View.GONE);
                else {
                    registerBinding.errorMsg.setVisibility(View.VISIBLE);
                    registerBinding.errorMsg.setText("Passwords does not match !");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerBinding.toLogin.setOnClickListener(view -> getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.left_to_right_slider, R.anim.right_to_left_slider)
                .replace(R.id.frame, new LoginFragment()).commit());

        return registerBinding.getRoot();
    }

    private void registerUser(String name, String email, String mobile, String password, String cPassword) {
        if (name.length() == 0 || email.length() == 0 || mobile.length() == 0 || password.length() == 0 || cPassword.length() == 0) {
            registerBinding.errorMsg.setVisibility(View.VISIBLE);
            registerBinding.errorMsg.setText("All fields are mandatory");
            return;
        }

        if (!password.equals(cPassword)) return;

        if (email.matches(Constants.emailPattern)) {
            if (mobile != null && mobile.length() > 8) {
                registerBinding.errorMsg.setVisibility(View.GONE);
                progressDialog.showDialog();
                toRegisterProcess(name, email, mobile, password);
            } else {
                registerBinding.errorMsg.setVisibility(View.VISIBLE);
                registerBinding.errorMsg.setText("Please enter a valid number");
            }
        } else {
            registerBinding.errorMsg.setVisibility(View.VISIBLE);
            registerBinding.errorMsg.setText("Please enter a valid email address");
        }
    }

    private void toRegisterProcess(String name, String email, String mobile, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.name, name);
        map.put(Constants.email, email);
        map.put(Constants.mobile, mobile);
        map.put(Constants.password, password);

        Call<verify_response_model> call = ApiControllers.getInstance()
                .getApi()
                .register(map);

        call.enqueue(new Callback<verify_response_model>() {
            @Override
            public void onResponse(Call<verify_response_model> call, Response<verify_response_model> response) {
                verify_response_model data = response.body();
                if (response.body().getMessage().equals("User created successfully")) {
                    RegisterToFirebase(data);
                } else if(data.getMessage().equals("User already exists")) {
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_found), "Registration Cannot Proceed", getResources().getColor(R.color.theme_color), response.body().getMessage() + "\nPlease login to the account.");
                    progressDialog.dismissDialog();
                } else {
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Registration Failed", getResources().getColor(R.color.red), response.body().getMessage());
                    progressDialog.dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<verify_response_model> call, Throwable t) {
                progressDialog.dismissDialog();
                dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), "Something went wrong.\nPlease try again later");
            }
        });
    }

    private void RegisterToFirebase(verify_response_model data) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.name, data.getResult().getName());
        map.put(Constants.email, data.getResult().getEmail());
        map.put(Constants.mobile, data.getResult().getMobile());
        map.put(Constants.mongoId, data.getResult().get_id());
        map.put(Constants.status, data.getResult().getStatus());
        map.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));

        database.collection(Constants.FIREBASE_DOCTORS_DB)
                .add(map)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismissDialog();
                    preferenceManager.putBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN, true);
                    preferenceManager.putString(Constants.token, data.getToken());
                    preferenceManager.putString(Constants.name, data.getResult().getName());
                    preferenceManager.putString(Constants.email, data.getResult().getEmail());
                    preferenceManager.putString(Constants.mongoId, data.getResult().get_id());
                    preferenceManager.putString(Constants.mobile, data.getResult().getMobile());
                    preferenceManager.putString(Constants.status, data.getResult().getStatus());
                    preferenceManager.putString(Constants.KEY_FIREBASE_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.isFull, data.getResult().getIsFull());


                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismissDialog();
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), e.getMessage().toString());
                });
    }
}