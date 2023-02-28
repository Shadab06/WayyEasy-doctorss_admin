package com.wayyeasy.wayyeasydoctors.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class prescription_response_model implements Parcelable {
    String medType, medName, medDesc;

    public prescription_response_model() {

    }

    public prescription_response_model(String medType, String medName, String medDesc) {
        this.medType = medType;
        this.medName = medName;
        this.medDesc = medDesc;
    }

    protected prescription_response_model(Parcel in) {
        medType = in.readString();
        medName = in.readString();
        medDesc = in.readString();
    }

    public static final Creator<prescription_response_model> CREATOR = new Creator<prescription_response_model>() {
        @Override
        public prescription_response_model createFromParcel(Parcel in) {
            return new prescription_response_model(in);
        }

        @Override
        public prescription_response_model[] newArray(int size) {
            return new prescription_response_model[size];
        }
    };

    public String getMedType() {
        return medType;
    }

    public void setMedType(String medType) {
        this.medType = medType;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedDesc() {
        return medDesc;
    }

    public void setMedDesc(String medDesc) {
        this.medDesc = medDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(medType);
        parcel.writeString(medName);
        parcel.writeString(medDesc);
    }
}
