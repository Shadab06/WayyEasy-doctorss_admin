package com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class single_user_booked implements Parcelable {
    private String userId, consultation, name, age, amountPaid, profileImage, fcmToken;

    public single_user_booked() {
    }

    public single_user_booked(String userId, String consultation, String name, String age, String amountPaid, String profileImage, String fcmToken) {
        this.userId = userId;
        this.consultation = consultation;
        this.name = name;
        this.age = age;
        this.amountPaid = amountPaid;
        this.profileImage = profileImage;
        this.fcmToken = fcmToken;
    }

    protected single_user_booked(Parcel in) {
        userId = in.readString();
        consultation = in.readString();
        name = in.readString();
        age = in.readString();
        amountPaid = in.readString();
        profileImage = in.readString();
        fcmToken = in.readString();
    }

    public static final Creator<single_user_booked> CREATOR = new Creator<single_user_booked>() {
        @Override
        public single_user_booked createFromParcel(Parcel in) {
            return new single_user_booked(in);
        }

        @Override
        public single_user_booked[] newArray(int size) {
            return new single_user_booked[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getConsultation() {
        return consultation;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(consultation);
        parcel.writeString(name);
        parcel.writeString(age);
        parcel.writeString(amountPaid);
        parcel.writeString(profileImage);
        parcel.writeString(fcmToken);
    }
}
