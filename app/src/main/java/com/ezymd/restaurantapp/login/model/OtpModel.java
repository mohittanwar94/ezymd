package com.ezymd.restaurantapp.login.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpModel implements Serializable {
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message = "";


    @SerializedName("data")
    private Otp data;

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public Otp getData() {
        return data;
    }

    public void setData(Otp data) {
        this.data = data;
    }
}
