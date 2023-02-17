package com.wayyeasy.wayyeasydoctors.Fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wayyeasy.wayyeasydoctors.Activities.DashboardActivity;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.NetworkChangeListener;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.FragmentLoginBinding;
import java.io.IOException;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    FragmentLoginBinding loginBinding;
    ResponseDialog dialog;
    ProgressDialog progressDialog;
    SharedPreferenceManager preferenceManager;

    FirebaseFirestore database;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    public static final String TAG = "Login Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        loginBinding.loginHeading.setPaintFlags(loginBinding.loginHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        database = FirebaseFirestore.getInstance();
        dialog = new ResponseDialog();
        progressDialog = new ProgressDialog(getActivity());
        preferenceManager = new SharedPreferenceManager(getActivity());

        loginBinding.loginBtn.setOnClickListener(view -> {
            if (loginBinding.emailMobile.getText().toString().length() > 9) {
                if (loginBinding.password.getText().toString().equals("") && loginBinding.password.getText().length() < 8) {
                    loginBinding.errorMsg.setVisibility(View.VISIBLE);
                    loginBinding.errorMsg.setText("Password should be of 8 characters.");
                } else {
                    loginBinding.errorMsg.setVisibility(View.GONE);
                    progressDialog.showDialog();
                    loginUser(loginBinding.emailMobile.getText().toString().trim(), loginBinding.password.getText().toString().trim());
                }
            } else {
                loginBinding.errorMsg.setVisibility(View.VISIBLE);
                loginBinding.errorMsg.setText("Please enter proper email or mobile !!!");
            }
        });

        loginBinding.toRegister.setOnClickListener(view -> {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.left_to_right_slider, R.anim.right_to_left_slider)
                    .replace(R.id.frame, new RegisterFragment()).commit();
        });

        return loginBinding.getRoot();
    }

    private void loginUser(String email, String password) {

        HashMap<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        Call<verify_response_model> call = ApiControllers.getInstance()
                .getApi()
                .login(loginData);

        call.enqueue(new Callback<verify_response_model>() {
            @Override
            public void onResponse(Call<verify_response_model> call, Response<verify_response_model> response) {
                verify_response_model data = response.body();
                Log.d(TAG, "onResponse: "+response.body());
                if (data.getMessage().equals("Login Successful")) {
                    AuthenticateToFirebase(data);
                } else if (data.getMessage().equals("User not found")) {
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Registration Failed", getResources().getColor(R.color.red), response.body().getMessage() + "\n\nPlease try creating new account.");
                    progressDialog.dismissDialog();
                } else {
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Registration Failed", getResources().getColor(R.color.red), response.body().getMessage());
                    progressDialog.dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<verify_response_model> call, Throwable t) {
                progressDialog.dismissDialog();
                Log.d(TAG, "onFailure: " + t.getMessage());
                dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), "Something went wrong.\nPlease try again later");
            }
        });
    }

    private void AuthenticateToFirebase(verify_response_model data) {
        database.collection(Constants.FIREBASE_DOCTORS_DB)
                .whereEqualTo(Constants.email, data.getResult().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        Runnable runnable = () -> updateFCMToken(data.getToken());

                        Thread thread = new Thread(runnable);
                        thread.start();

                        progressDialog.dismissDialog();
                        preferenceManager.putBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN, true);
                        preferenceManager.putString(Constants.token, data.getToken());
                        preferenceManager.putString(Constants.name, data.getResult().getName());
                        preferenceManager.putString(Constants.email, data.getResult().getEmail());
                        preferenceManager.putString(Constants.mongoId, data.getResult().get_id());
                        preferenceManager.putString(Constants.mobile, data.getResult().getMobile());
                        preferenceManager.putString(Constants.qualification, data.getResult().getQualification());
                        preferenceManager.putString(Constants.specialityType, data.getResult().getSpecialityType());
                        preferenceManager.putString(Constants.description, data.getResult().getDescription());
                        preferenceManager.putString(Constants.price, data.getResult().getPrice());
                        preferenceManager.putString(Constants.image, data.getResult().getImage());
                        preferenceManager.putString(Constants.status, data.getResult().getStatus());
                        preferenceManager.putString(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                        preferenceManager.putString(Constants.KEY_FIREBASE_USER_ID, task.getResult().getDocuments().get(0).getId());
                        preferenceManager.putString(Constants.isFull, data.getResult().getIsFull());

                        Intent intent = new Intent(getActivity(), DashboardActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        RegisterToFirebase(data);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismissDialog();
                    dialog.showDialog(getActivity(), getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), e.getMessage().toString());
                });
    }

    private void updateFCMToken(String token) {

        HashMap<String, String> map = new HashMap<>();
        map.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));

        Call<Void> call = ApiControllers.getInstance()
                .getApi()
                .updateToken("Bearer " + token, map);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Failed to update token\nPlease login again.", Toast.LENGTH_SHORT).show();
        }
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
                    preferenceManager.putString(Constants.qualification, data.getResult().getQualification());
                    preferenceManager.putString(Constants.specialityType, data.getResult().getSpecialityType());
                    preferenceManager.putString(Constants.description, data.getResult().getDescription());
                    preferenceManager.putString(Constants.price, data.getResult().getPrice());
                    preferenceManager.putString(Constants.image, data.getResult().getImage());
                    preferenceManager.putString(Constants.status, data.getResult().getStatus());
                    preferenceManager.putString(Constants.KEY_FCM_TOKEN, data.getResult().getFcmToken());
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

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}