package com.wayyeasy.wayyeasydoctors.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ProgressDialog;
import com.wayyeasy.wayyeasydoctors.CustomDialogs.ResponseDialog;
import com.wayyeasy.wayyeasydoctors.Models.prescription_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model_sub;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityPrescriptionBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionActivity extends AppCompatActivity {

    ActivityPrescriptionBinding prescriptionBinding;

    public static final String TAG = "Prescription activity";

    List<String> medicineTypeList = new ArrayList<>();
    List<prescription_response_model> prescriptionItems = new ArrayList<>();
    List<prescription_response_model> userPrescription = new ArrayList<>();

    SharedPreferenceManager preferenceManager;
    ProgressDialog progressDialog;
    ResponseDialog dialog;

    AutoCompleteTextView medicineType;
    TextInputEditText medicineName, medicineDesc;

    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prescriptionBinding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(prescriptionBinding.getRoot());

        preferenceManager = new SharedPreferenceManager(getApplicationContext());
        progressDialog = new ProgressDialog(PrescriptionActivity.this);
        dialog = new ResponseDialog();

        userId = getIntent().getStringExtra("userId");
        userPrescription.clear();
        userPrescription = getIntent().getParcelableArrayListExtra("prescription");
        if (userPrescription != null && userPrescription.size() > 0) {
            for (int i = 0; i < userPrescription.size(); i++) {
                addView();

                View medicineView = prescriptionBinding.medicineList.getChildAt(i);

                medicineType = medicineView.findViewById(R.id.medicine_type);
                medicineName = medicineView.findViewById(R.id.medicine_name);
                medicineDesc = medicineView.findViewById(R.id.medicine_description);

                medicineName.setText(userPrescription.get(i).getMedName());
                medicineType.setText(userPrescription.get(i).getMedType());
                medicineDesc.setText(userPrescription.get(i).getMedDesc());
            }
        };

        medicineTypeList.add("Tab");
        medicineTypeList.add("Syrup");
        medicineTypeList.add("Cap");
        medicineTypeList.add("Powder");
        medicineTypeList.add("Test");
    }

    public void addPrescription(View view) {
        addView();
    }

    private void addView() {
        View medicineView = getLayoutInflater().inflate(R.layout.add_medicine, null, false);

        AutoCompleteTextView medicineType = medicineView.findViewById(R.id.medicine_type);
        ImageView deleteMedicine = medicineView.findViewById(R.id.delete_medicine);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, medicineTypeList);
        medicineType.setAdapter(arrayAdapter);

        prescriptionBinding.medicineList.addView(medicineView);

        deleteMedicine.setOnClickListener(view -> deleteView(medicineView));
    }

    private void deleteView(View view) {
        prescriptionBinding.medicineList.removeView(view);
    }

    public void clearActivity(View view) {
        finish();
    }

    private boolean checkAllFieldsEntered() {
        prescriptionItems.clear();
        boolean result = true;

        for (int i = 0; i < prescriptionBinding.medicineList.getChildCount(); i++) {

            View medicineView = prescriptionBinding.medicineList.getChildAt(i);

            medicineType = medicineView.findViewById(R.id.medicine_type);
            medicineName = medicineView.findViewById(R.id.medicine_name);
            medicineDesc = medicineView.findViewById(R.id.medicine_description);

            prescription_response_model prescriptionItem = new prescription_response_model();

            if (!medicineName.getText().toString().equals("")) {
                prescriptionItem.setMedName(medicineName.getText().toString());
            } else {
                result = false;
                break;
            }

            if (!medicineDesc.getText().toString().equals("")) {
                prescriptionItem.setMedDesc(medicineDesc.getText().toString());
            } else {
                result = false;
                break;
            }

            if (!medicineType.getText().toString().equals("")) {
                prescriptionItem.setMedType(medicineType.getText().toString());
            } else {
                result = false;
                break;
            }

            prescriptionItems.add(prescriptionItem);

        }

        if (prescriptionItems.size() == 0) {
            result = false;
            Toast.makeText(this, "Empty prescription cannot be uploaded !!!", Toast.LENGTH_SHORT).show();
        } else if (!result) {
            Toast.makeText(this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public void uploadPrescription(View view) {
        if (checkAllFieldsEntered()) {
            Log.d(TAG, "uploadPrescription: 169 "+prescriptionItems.size());
            progressDialog.showDialog();

            Call<verify_response_model> call = ApiControllers.getInstance()
                    .getApi()
                    .addPrescription("Bearer " + preferenceManager.getString(Constants.token), userId, prescriptionItems);

            call.enqueue(new Callback<verify_response_model>() {
                @Override
                public void onResponse(Call<verify_response_model> call, Response<verify_response_model> response) {
                    progressDialog.dismissDialog();
                    if (response.body() != null && response.body().getMessage() != null)
                        dialog.showDialog(PrescriptionActivity.this, getResources().getDrawable(R.drawable.ic_success), "Successful", getResources().getColor(R.color.green), response.body().getMessage());
                    else
                        dialog.showDialog(PrescriptionActivity.this, getResources().getDrawable(R.drawable.ic_found), "Failed", getResources().getColor(R.color.red), response.body().toString());
                }

                @Override
                public void onFailure(Call<verify_response_model> call, Throwable t) {
                    progressDialog.dismissDialog();
                    Log.d(TAG, "onFailure: 219 " + t.getMessage());
                    dialog.showDialog(PrescriptionActivity.this, getResources().getDrawable(R.drawable.ic_error), "Error", getResources().getColor(R.color.red), t.getMessage());
                }
            });
        }
    }
}