package com.wayyeasy.wayyeasydoctors.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class single_user_response_model implements Parcelable {
    String amountPaid;
    ArrayList<prescription_response_model> prescription;

    public single_user_response_model() {
    }

    public single_user_response_model(String amountPaid, ArrayList<prescription_response_model> prescription) {
        this.amountPaid = amountPaid;
        this.prescription = prescription;
    }

    protected single_user_response_model(Parcel in) {
        amountPaid = in.readString();
        prescription = in.createTypedArrayList(prescription_response_model.CREATOR);
    }

    public static final Creator<single_user_response_model> CREATOR = new Creator<single_user_response_model>() {
        @Override
        public single_user_response_model createFromParcel(Parcel in) {
            return new single_user_response_model(in);
        }

        @Override
        public single_user_response_model[] newArray(int size) {
            return new single_user_response_model[size];
        }
    };

    public String getAmountPaid() {
        return amountPaid;
    }

    public ArrayList<prescription_response_model> getPrescription() {
        return prescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(amountPaid);
        parcel.writeTypedList(prescription);
    }
}
