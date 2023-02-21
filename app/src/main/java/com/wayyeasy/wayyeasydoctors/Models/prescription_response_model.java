package com.wayyeasy.wayyeasydoctors.Models;

public class prescription_response_model {
    String medType, medName, medDesc;

    public prescription_response_model() {

    }

    public prescription_response_model(String medType, String medName, String medDesc) {
        this.medType = medType;
        this.medName = medName;
        this.medDesc = medDesc;
    }

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
}
