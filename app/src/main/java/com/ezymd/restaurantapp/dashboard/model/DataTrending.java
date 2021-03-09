package com.ezymd.restaurantapp.dashboard.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataTrending implements Serializable {
    @SerializedName("rating")
    @Expose
    private Double rating = 0.0;


    @SerializedName("id")
    @Expose
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("phone_no")
    @Expose
    private String phone_no;

    @SerializedName("banner")
    @Expose
    private String banner;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    @SerializedName("cuisines")
    @Expose
    private String cuisines;

    @SerializedName("min_order")
    @Expose
    private String min_order;


    @SerializedName("discount")
    @Expose
    private String discount;

    @SerializedName("is_free_delivery")
    @Expose
    private String is_free_delivery;

    @SerializedName("lang")
    @Expose
    private String lang;


    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("city")
    @Expose
    private String city;


    @SerializedName("lat")
    @Expose
    private String lat;


    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public String getMin_order() {
        return TextUtils.isEmpty(min_order)?"0":min_order;
    }

    public void setMin_order(String min_order) {
        this.min_order = min_order;
    }

    public String getDiscount() {
        return TextUtils.isEmpty(discount) ? "0" : discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getIs_free_delivery() {
        return TextUtils.isEmpty(is_free_delivery)?"0":is_free_delivery;
    }

    public void setIs_free_delivery(String is_free_delivery) {
        this.is_free_delivery = is_free_delivery;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
