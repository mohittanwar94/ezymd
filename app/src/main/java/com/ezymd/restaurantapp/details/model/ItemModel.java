package com.ezymd.restaurantapp.details.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit2.SkipCallbackExecutor;

public class ItemModel {

    @SerializedName("rating")
    @Expose
    private Double rating = 0.0;

    @SerializedName("stock")
    @Expose
    private int stock;

    @SerializedName("sell_price")
    @Expose
    private int sell_price;

    @SerializedName("price")
    @Expose
    private int price;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    @SerializedName("item")
    @Expose
    private String item;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("category")
    @Expose
    private String category;


    @SerializedName("image")
    @Expose
    private String image;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSell_price() {
        return sell_price;
    }

    public void setSell_price(int sell_price) {
        this.sell_price = sell_price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}