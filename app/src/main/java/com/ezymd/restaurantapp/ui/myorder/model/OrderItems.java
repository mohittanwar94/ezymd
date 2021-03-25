package com.ezymd.restaurantapp.ui.myorder.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderItems implements Serializable {

    @SerializedName("item")
    @Expose
    private String item;

    @SerializedName("description")
    @Expose
    private String description;


    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("qty")
    @Expose
    private Integer qty;

    @SerializedName("id")
    @Expose
    private Integer id = 0;

    @SerializedName("product_option_id")
    @Expose
    private String product_option_id = "";

    @SerializedName("product_option_name")
    @Expose
    private String product_option_name = "";

    @SerializedName("image")
    @Expose
    private String image = "";


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }


    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(String product_option_id) {
        this.product_option_id = product_option_id;
    }

    public String getProduct_option_name() {
        return TextUtils.isEmpty(product_option_name) ? "" : product_option_name;
    }

    public void setProduct_option_name(String product_option_name) {
        this.product_option_name = product_option_name;
    }
}
