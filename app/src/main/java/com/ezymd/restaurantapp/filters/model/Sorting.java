package com.ezymd.restaurantapp.filters.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Sorting {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("data")
    @Expose
    private ArrayList<Sort> data = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Sort> getData() {
        return data;
    }

    public void setData(ArrayList<Sort> data) {
        this.data = data;
    }

}