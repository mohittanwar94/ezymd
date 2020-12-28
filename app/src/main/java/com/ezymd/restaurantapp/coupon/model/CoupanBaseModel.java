package com.ezymd.restaurantapp.coupon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CoupanBaseModel {
    @SerializedName("data")
    @Expose
    private ArrayList<CoupanModel> data = new ArrayList<CoupanModel>();
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;


    public ArrayList<CoupanModel> getData() {
        return data;
    }

    public void setData(ArrayList<CoupanModel> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
