package com.ezymd.restaurantapp.filters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterInnerModel {
    @SerializedName("filter_value_id")
    @Expose
    private Integer filterValueId;
    @SerializedName("filter_value_name")
    @Expose
    private String filterValueName;

    private boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Integer getFilterValueId() {
        return filterValueId;
    }

    public void setFilterValueId(Integer filterValueId) {
        this.filterValueId = filterValueId;
    }

    public String getFilterValueName() {
        return filterValueName;
    }

    public void setFilterValueName(String filterValueName) {
        this.filterValueName = filterValueName;
    }
}
