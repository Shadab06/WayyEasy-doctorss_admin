package com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling;

import android.os.Parcel;
import android.os.Parcelable;

public class user_booked_response_model implements Parcelable {
    private String userId, consultation, name, age, amountPaid, profileImage, fcmToken;

    public user_booked_response_model() {
    }

    public user_booked_response_model(String userId, String consultation, String name, String age, String amountPaid, String profileImage, String fcmToken) {
        this.userId = userId;
        this.consultation = consultation;
        this.name = name;
        this.age = age;
        this.amountPaid = amountPaid;
        this.profileImage = profileImage;
        this.fcmToken = fcmToken;
    }

    protected user_booked_response_model(Parcel in) {
        userId = in.readString();
        consultation = in.readString();
        name = in.readString();
        age = in.readString();
        amountPaid = in.readString();
        profileImage = in.readString();
        fcmToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(consultation);
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(amountPaid);
        dest.writeString(profileImage);
        dest.writeString(fcmToken);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
