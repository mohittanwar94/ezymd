package com.ezymd.restaurantapp.utils;

import java.io.Serializable;

public class OrderCheckoutUtilsModel implements Serializable {


    private String deliveryAddress = "";

    private int resturant_id = 0;
    //1 for now 2 for schedule
    private int delivery_type = 1;
    private String delivery_time = "";
    private String delivery_instruction = "";

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public int getResturant_id() {
        return resturant_id;
    }

    public void setResturant_id(int resturant_id) {
        this.resturant_id = resturant_id;
    }

    public int getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(int delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getDelivery_instruction() {
        return delivery_instruction;
    }

    public void setDelivery_instruction(String delivery_instruction) {
        this.delivery_instruction = delivery_instruction;
    }
}
