package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.url;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wayyeasy.wayyeasydoctors.Adapters.PaidUsersList;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Listeners.UsersListener;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model_sub;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.NetworkChangeListener;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityDashboardBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    public static final String TAG = "Dashboard";

    ActivityDashboardBinding dashboard;
    ActionBarDrawerToggle toggle;
    private TextView doctorNameHeader, doctorSpecialityHeader, doctorRatingsHeader;
    private ImageView doctorImageHeader;
    private boolean isOnline = false;
    SharedPreferenceManager preferenceManager;
    ProgressDialog progressDialog;
    ResponseDialog dialog;
    PaidUsersList paidUsersList;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboard = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboard.getRoot());

        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        progressDialog = new ProgressDialog(DashboardActivity.this);
        dialog = new ResponseDialog();
        Log.d(TAG, "onCreate: 61 " + preferenceManager.getString(Constants.KEY_FCM_TOKEN));

        if (preferenceManager.getBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN)) {
            if (preferenceManager.getString(Constants.status).equals("pending")) {
                progressDialog.showDialog();

                FirebaseFirestore database = FirebaseFirestore.getInstance();

                database.collection(Constants.FIREBASE_DOCTORS_DB)
                        .whereEqualTo(Constants.mongoId, preferenceManager.getString(Constants.mongoId))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getString("status").equals("active")) {
                                        fetchProfileIfActive();
                                    } else if (document.getString("status").equals("pending")) {
                                        dialog.showDialog(DashboardActivity.this, getResources().getDrawable(R.drawable.ic_no_result), "Message", getResources().getColor(R.color.theme_color_orange), "Your profile is not active yet.");
                                        progressDialog.dismissDialog();
                                    } else {
                                        dialog.showDialog(DashboardActivity.this, getResources().getDrawable(R.drawable.ic_no_result), "Message", getResources().getColor(R.color.theme_color_orange), "No data found.\n\nIf you have requested profile update please contact our customer support.");
                                        progressDialog.dismissDialog();
                                    }
                                }
                            } else {
                                progressDialog.dismissDialog();
                                dialog.showDialog(DashboardActivity.this, getResources().getDrawable(R.drawable.ic_error), "Alert", getResources().getColor(R.color.red), "We cannot fetch your data now but it will get updated in the time span.");
                            }
                        });
            }
            if (preferenceManager.getString(Constants.status).equals("active")) {
                if (preferenceManager.getString(Constants.KEY_FCM_TOKEN) == null) {
                    progressDialog.showDialog();
                    fetchProfileIfActive();
                } else
                    fetchUsersPaidForConsult();
            }
        }

        //Menu starts
        setSupportActionBar(dashboard.toolbar);

        toggle = new ActionBarDrawerToggle(this, dashboard.navDrawer, dashboard.toolbar, R.string.opened, R.string.closed);
        dashboard.navDrawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        dashboard.navMenu.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.notification:
                    dashboard.navDrawer.closeDrawer(androidx.core.view.GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                    break;

                case R.id.earnings:
                    dashboard.navDrawer.closeDrawer(androidx.core.view.GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), EarningsActivity.class));
                    break;

                case R.id.contact:
                    dashboard.navDrawer.closeDrawer(androidx.core.view.GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                    break;

                case R.id.terms_conditions:
                    dashboard.navDrawer.closeDrawer(androidx.core.view.GravityCompat.START);
                    Intent toTerms = new Intent(getApplicationContext(), PrivacyActivity.class);
                    toTerms.putExtra("policiesLink", "file:///android_asset/terms_conditions.html");
                    startActivity(toTerms);
                    break;

                case R.id.privacy:
                    dashboard.navDrawer.closeDrawer(androidx.core.view.GravityCompat.START);
                    Intent toPolicy = new Intent(getApplicationContext(), PrivacyActivity.class);
                    toPolicy.putExtra("policiesLink", "file:///android_asset/privacy_policy.html");
                    startActivity(toPolicy);
                    break;
            }
            return true;
        });

        View header = dashboard.navMenu.getHeaderView(0);
        doctorNameHeader = header.findViewById(R.id.doctor_name_header);
        doctorSpecialityHeader = header.findViewById(R.id.doctor_speciality_header);
        doctorRatingsHeader = header.findViewById(R.id.user_rating_header);
        doctorImageHeader = header.findViewById(R.id.doctor_profile_header);

        header.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
        //Menu ends

        updateDoctorsData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.on_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.availability:
                isOnline = !isOnline;
                updateAvailability(item, isOnline);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAvailability(MenuItem item, boolean isOnline) {
        if (preferenceManager.getBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN)) {
            if (preferenceManager.getString(Constants.status).equals("active")) {
                if (isOnline) {
                    item.setIcon(R.drawable.offline);
                    dashboard.toolbar.setTitle("You are online now");
                } else {
                    item.setIcon(R.drawable.online);
                    dashboard.toolbar.setTitle("Offline");
                }
            } else {
                Toast.makeText(this, "Profile is not active yet.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toProfileActivity(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void fetchProfileIfActive() {
        Call<verify_response_model_sub> call = ApiControllers.getInstance()
                .getApi()
                .getPhysicianById("Bearer " + preferenceManager.getString(Constants.token), preferenceManager.getString(Constants.mongoId));

        call.enqueue(new Callback<verify_response_model_sub>() {
            @Override
            public void onResponse(Call<verify_response_model_sub> call, Response<verify_response_model_sub> response) {
                if (response.code() == 200) {
                    verify_response_model_sub data = response.body();
                    preferenceManager.putBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN, true);
                    preferenceManager.putString(Constants.mongoId, data.get_id());
                    preferenceManager.putString(Constants.role, data.getRole());
                    preferenceManager.putString(Constants.name, data.getName());
                    preferenceManager.putString(Constants.isFull, data.getIsFull());
                    preferenceManager.putString(Constants.address, data.getAddress());
                    preferenceManager.putString(Constants.description, data.getDescription());
                    preferenceManager.putString(Constants.qualification, data.getQualification());
                    preferenceManager.putString(Constants.specialityType, data.getSpecialityType());
                    preferenceManager.putString(Constants.mobile, data.getMobile());
                    preferenceManager.putString(Constants.price, data.getPrice());
                    preferenceManager.putString(Constants.image, data.getImage());
                    preferenceManager.putString(Constants.status, data.getStatus());
                    preferenceManager.putString(Constants.KEY_FCM_TOKEN, data.getFcmToken());

                    progressDialog.dismissDialog();
                    updateDoctorsData();
                }
            }

            @Override
            public void onFailure(Call<verify_response_model_sub> call, Throwable t) {
                progressDialog.dismissDialog();
                Log.d(TAG, "onFailure: 219 " + t.getMessage());
                dialog.showDialog(DashboardActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), t.getMessage());
            }
        });
    }

    private void updateDoctorsData() {
        if (preferenceManager.getBoolean(Constants.KEY_IS_DOCTOR_SIGNED_IN)) {
            if (preferenceManager.getString(Constants.name) != null && preferenceManager.getString(Constants.name).length() > 0)
                doctorNameHeader.setText(preferenceManager.getString(Constants.name));

            if (preferenceManager.getString(Constants.specialityType) != null && preferenceManager.getString(Constants.specialityType).length() > 0)
                doctorSpecialityHeader.setText(preferenceManager.getString(Constants.specialityType));

            if (preferenceManager.getString(Constants.ratings) != null && preferenceManager.getString(Constants.ratings).length() > 0)
                doctorRatingsHeader.setText(preferenceManager.getString(Constants.ratings));

            if (preferenceManager.getString(Constants.image) != null && preferenceManager.getString(Constants.image).length() > 0) {

                String imgUrl = url + "doctors/" + preferenceManager.getString(Constants.image);

                GlideUrl glideUrl = new GlideUrl(imgUrl,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + preferenceManager.getString(Constants.token))
                                .build());

                Glide.with(DashboardActivity.this).load(glideUrl).into(doctorImageHeader);
            }

            if (preferenceManager.getString(Constants.status).equals("inActive")) {
                dashboard.inActiveMsg.setVisibility(View.VISIBLE);
            }
            if (preferenceManager.getString(Constants.status).equals("pending")) {
                dashboard.inActiveMsg.setVisibility(View.VISIBLE);
                dashboard.statusMeg.setText("Profile activation request has been sent.\nWe will update you within 1 day.");
                dashboard.statusBtn.setText("Visit Profile Page");
            }
            if (preferenceManager.getString(Constants.status).equals("active")) {
                dashboard.inActiveMsg.setVisibility(View.GONE);
            }
        }
    }

    private void fetchUsersPaidForConsult() {
        progressDialog.showDialog();
        Call<List<user_booked_response_model>> call = ApiControllers.getInstance()
                .getApi()
                .getPhysicianPaidUsers("Bearer " + preferenceManager.getString(Constants.token), preferenceManager.getString(Constants.mongoId));

        call.enqueue(new Callback<List<user_booked_response_model>>() {
            @Override
            public void onResponse(Call<List<user_booked_response_model>> call, Response<List<user_booked_response_model>> response) {
                progressDialog.dismissDialog();
                if (response != null && response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    dashboard.recyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                    List<user_booked_response_model> usersList = response.body();
                    paidUsersList = new PaidUsersList(usersList, preferenceManager.getString(Constants.token));
                    dashboard.recyclerView.setAdapter(paidUsersList);
                }
            }

            @Override
            public void onFailure(Call<List<user_booked_response_model>> call, Throwable t) {
                progressDialog.dismissDialog();
                Log.d(TAG, "onFailure: 322 " + t.getMessage());
                dialog.showDialog(DashboardActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}