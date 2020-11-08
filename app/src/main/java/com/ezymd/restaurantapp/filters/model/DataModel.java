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
