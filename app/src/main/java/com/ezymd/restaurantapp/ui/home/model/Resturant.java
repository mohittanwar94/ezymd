package com.ezymd.restaurantapp.ui.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Resturant implements Serializable {


    @SerializedName("address")
    @Expose
    private String address = "";

    @SerializedName("lat")
    @Expose
    private Double lat = 0.0;

    @SerializedName("long")
    @Expose
    private Double longitude = 0.0;
    @SerializedName("id")
    @Expose
    private Integer id = 0;
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("banner")
    @Expose
    private String banner = "";
    @SerializedName("category")
    @Expose
    private String category = "";
    @SerializedName("cuisines")
    @Expose
    private String cuisines = "";
    @SerializedName("rating")
    @Expose
    private Double rating = 0.0;
    @SerializedName("min_order")
    @Expose
    private String minOrder = "0";
    @SerializedName("discount")
    @Expose
    private Integer discount = 0;
    @SerializedName("is_free_delivery")
    @Expose
    private Integer isFreeDelivery = 0;
    @SerializedName("distance")
    @Expose
    private Double distance = 0.0;

    private boolean isPick = false;

    public boolean isPick() {
        return isPick;
    }

    public void setPick(boolean pick) {
        isPick = pick;
    }

    public String getAddress() {
        return address != null && !address.equals("") ? address : "N/A";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public Double getRating() {
        return rating != null ? rating : 0;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getMinOrder() {
        return minOrder != null && !minOrder.equals("") ? minOrder : "0";
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public Integer getDiscount() {
        return discount != null ? discount : 0;

    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getIsFreeDelivery() {
        return isFreeDelivery;
    }

    public void setIsFreeDelivery(Integer isFreeDelivery) {
        this.isFreeDelivery = isFreeDelivery;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}

