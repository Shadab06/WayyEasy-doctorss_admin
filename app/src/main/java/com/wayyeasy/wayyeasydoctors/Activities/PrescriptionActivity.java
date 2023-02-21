package com.wayyeasy.wayyeasydoctors.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.wayyeasy.wayyeasydoctors.Models.prescription_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityPrescriptionBinding;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {

    ActivityPrescriptionBinding prescriptionBinding;

    List<String> medicineTypeList = new ArrayList<>();
    List<prescription_response_model> prescriptionItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prescriptionBinding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(prescriptionBinding.getRoot());

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
        TextInputEditText medicineName = medicineView.findViewById(R.id.medicine_name);
        TextInputEditText medicineDesc = medicineView.findViewById(R.id.medicine_description);
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

    public void submitPrescription(View view) {
        if (checkAllFiendsEntered()) {
            for (int i = 0; i < prescriptionItems.size(); i++) {
                Toast.makeText(this, prescriptionItems.get(i).getMedName() + ' ' + prescriptionItems.get(i).getMedDesc() + ' ' + prescriptionItems.get(i).getMedType(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAllFiendsEntered() {
        prescriptionItems.clear();
        boolean result = true;

        for (int i = 0; i < prescriptionBinding.medicineList.getChildCount(); i++) {

            View medicineView = prescriptionBinding.medicineList.getChildAt(i);

            AutoCompleteTextView medicineType = medicineView.findViewById(R.id.medicine_type);
            TextInputEditText medicineName = medicineView.findViewById(R.id.medicine_name);
            TextInputEditText medicineDesc = medicineView.findViewById(R.id.medicine_description);

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
}