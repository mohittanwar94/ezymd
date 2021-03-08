package com.ezymd.restaurantapp.dashboard.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TrendingDashboardModel implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<DataTrending> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;

    public ArrayList<DataTrending> getData() {
        return data;
    }

    public void setData(ArrayList<DataTrending> data) {
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
