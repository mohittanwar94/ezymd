package com.ezymd.restaurantapp.details.model;

import com.ezymd.restaurantapp.ui.home.model.Trending;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MenuItemModel {
    @SerializedName("data")
    @Expose
    private ArrayList<ItemModel> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;

    public ArrayList<ItemModel> getData() {
        return data;
    }

    public void setData(ArrayList<ItemModel> data) {
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
