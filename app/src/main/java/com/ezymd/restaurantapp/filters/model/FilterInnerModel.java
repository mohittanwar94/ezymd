package com.ezymd.restaurantapp.filters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class FilterInnerModel {
    @SerializedName("filter_value_id")
    @Expose
    private Integer filterValueId;
    @SerializedName("filter_value_name")
    @Expose
    private String filterValueName;

    private boolean isSelected = false;

    @SerializedName("sort_by")
    @Expose
    private String sort_by = "";


    @SerializedName("sort_order")
    @Expose
    private String sort_order = "";


    private boolean isSingleSelected = false;
    @NotNull
    public final int itemType = 0;

    public boolean isSingleSelected() {
        return isSingleSelected;
    }

    public void setSingleSelected(boolean singleSelected) {
        isSingleSelected = singleSelected;
    }


    public String getSort_by() {
        return sort_by;
    }

    public void setSort_by(String sort_by) {
        this.sort_by = sort_by;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

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
