package com.ezymd.restaurantapp.ui.myorder.model;

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

    public void setPrice(Integer price) {
        this.price = price;
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
}
