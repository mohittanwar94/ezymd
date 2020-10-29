package com.ezymd.restaurantapp.ui.home.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Trending implements Serializable {

    @SerializedName("food_id")
    @Expose
    private Integer foodId;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}