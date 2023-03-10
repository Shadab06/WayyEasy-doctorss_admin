package com.wayyeasy.wayyeasydoctors.Models;

public class verify_response_model_sub {
    private String name, image, _id, role, mobile, email, isFull, status, badge, address, description, price, shiftStart, shiftEnd, proofDocs, qualification, specialityType, fcmToken;

    public verify_response_model_sub() {
    }

    public verify_response_model_sub(String name, String image, String _id, String role, String mobile, String email, String isFull, String status, String badge, String address, String description, String price, String shiftStart, String shiftEnd, String proofDocs, String qualification, String specialityType, String fcmToken) {
        this.name = name;
        this.image = image;
        this._id = _id;
        this.role = role;
        this.mobile = mobile;
        this.email = email;
        this.isFull = isFull;
        this.status = status;
        this.badge = badge;
        this.address = address;
        this.description = description;
        this.price = price;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.proofDocs = proofDocs;
        this.qualification = qualification;
        this.specialityType = specialityType;
        this.fcmToken = fcmToken;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String get_id() {
        return _id;
    }

    public String getRole() {
        return role;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getIsFull() {
        return isFull;
    }

    public String getStatus() {
        return status;
    }

    public String getBadge() {
        return badge;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public String getProofDocs() {
        return proofDocs;
    }

    public String getQualification() {
        return qualification;
    }

    public String getSpecialityType() {
        return specialityType;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
