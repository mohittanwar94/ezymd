package com.ezymd.restaurantapp.login.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpModel implements Serializable {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message="";

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
