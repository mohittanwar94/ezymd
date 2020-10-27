package com.ezymd.restaurantapp.login.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpModel implements Serializable {
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message="";

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
}
