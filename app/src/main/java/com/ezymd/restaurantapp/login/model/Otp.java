package com.ezymd.restaurantapp.login.model;

import com.google.gson.annotations.SerializedName;

public class Otp {
    @SerializedName("OTP")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
