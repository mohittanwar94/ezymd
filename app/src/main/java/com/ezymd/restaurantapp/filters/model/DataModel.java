package com.ezymd.restaurantapp.filters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataModel {
    @SerializedName("Filters")
    @Expose
    private ArrayList<Filter> filters = null;
    @SerializedName("Sorting")
    @Expose
    private Sorting sorting = null;


    private String rating = "0";


    private String min_price = "50";

    private String max_price = "1000";

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
    }

}
