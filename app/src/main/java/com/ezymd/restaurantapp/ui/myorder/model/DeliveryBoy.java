package com.ezymd.restaurantapp.ui.myorder.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeliveryBoy implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("photo")
    @Expose
    private String photo;

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return TextUtils.isEmpty(email) ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return TextUtils.isEmpty(phoneNo) ? "" : phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoto() {
        return TextUtils.isEmpty(photo) ? "" : photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
