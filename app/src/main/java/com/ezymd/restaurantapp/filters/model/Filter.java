package com.ezymd.restaurantapp.filters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Filter {

    @SerializedName("filter_id")
    @Expose
    private Integer filterId;
    @SerializedName("filter_name")
    @Expose
    private String filterName;
    @SerializedName("data")
    @Expose
    private ArrayList<FilterInnerModel> data = new ArrayList<>();

    public Integer getFilterId() {
        return filterId;
    }

    public void setFilterId(Integer filterId) {
        this.filterId = filterId;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public ArrayList<FilterInnerModel> getData() {
        return data;
    }

    public void setData(ArrayList<FilterInnerModel> data) {
        this.data = data;
    }
}