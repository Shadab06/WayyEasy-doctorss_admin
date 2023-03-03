package com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class user_booked_response_model implements Parcelable {
    private String result;
    private List<single_user_booked> data = new ArrayList<>();

    public user_booked_response_model() {
    }

    public user_booked_response_model(String result, List<single_user_booked> data) {
        this.result = result;
        this.data = data;
    }

    protected user_booked_response_model(Parcel in) {
        result = in.readString();
        data = in.createTypedArrayList(single_user_booked.CREATOR);
    }

    public static final Creator<user_booked_response_model> CREATOR = new Creator<user_booked_response_model>() {
        @Override
        public user_booked_response_model createFromParcel(Parcel in) {
            return new user_booked_response_model(in);
        }

        @Override
        public user_booked_response_model[] newArray(int size) {
            return new user_booked_response_model[size];
        }
    };

    public String getResult() {
        return result;
    }

    public List<single_user_booked> getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(result);
        parcel.writeTypedList(data);
    }
}