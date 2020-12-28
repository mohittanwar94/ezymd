package com.ezymd.restaurantapp.coupon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoupanModel implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_fixed")
    @Expose
    private Integer isFixed;
    @SerializedName("discount_value")
    @Expose
    private String discountValue;

    @SerializedName("description")
    @Expose
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Integer isFixed) {
        this.isFixed = isFixed;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
