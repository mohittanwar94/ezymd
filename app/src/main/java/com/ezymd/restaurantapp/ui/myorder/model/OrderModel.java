package com.ezymd.restaurantapp.ui.myorder.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderModel implements Serializable {
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("restaurant_name")
    @Expose
    private String restaurantName;
    @SerializedName("restaurant_address")
    @Expose
    private String restaurantAddress;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("delivery_instruction")
    @Expose
    private String deliveryInstruction;
    @SerializedName("schedule_type")
    @Expose
    private Integer scheduleType;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("order_pickup_status")
    @Expose
    private Integer orderPickupStatus;
    @SerializedName("restaurant_lat")
    @Expose
    private String restaurant_lat;
    @SerializedName("restaurant_lang")
    @Expose
    private String restaurant_lang;
    @SerializedName("delivery_lat")
    @Expose
    private String delivery_lat;
    @SerializedName("delivery_lang")
    @Expose
    private String delivery_lang;
    @SerializedName("order_created")
    @Expose
    private String created;
    @SerializedName("schedule_date")
    @Expose
    private String scheduleTime;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("order_status")
    @Expose
    private Integer orderStatus;
    @SerializedName("delivery_time")
    @Expose
    private String deliveryTime;
    @SerializedName("transaction_charges")
    @Expose
    private String transactionCharges;
    @SerializedName("delivery_charges")
    @Expose
    private String deliveryCharges;

    @SerializedName("order_items")
    @Expose
    private ArrayList<OrderItems> orderItems = new ArrayList<>();

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantName() {
        return TextUtils.isEmpty(restaurantName) ? "" : restaurantName;
    }

    public String getRestaurantAddress() {
        return TextUtils.isEmpty(restaurantAddress) ? "" : restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getTransactionCharges() {
        return transactionCharges;
    }

    public void setTransactionCharges(String transactionCharges) {
        this.transactionCharges = transactionCharges;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUsername() {
        return TextUtils.isEmpty(username) ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return TextUtils.isEmpty(email) ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return TextUtils.isEmpty(phoneNo) ? "" : phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Double getTotal() {
        return total;
    }

    public String getCreated() {
        return created;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getScheduleTime() {
        return TextUtils.isEmpty(scheduleTime) ? "" : scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getDeliveryInstruction() {
        return TextUtils.isEmpty(deliveryInstruction) ? "N/A" : deliveryInstruction;
    }

    public String getRestaurant_lang() {
        return TextUtils.isEmpty(restaurant_lang) ? "0.0" : restaurant_lang;
    }

    public void setRestaurant_lang(String restaurant_lang) {
        this.restaurant_lang = restaurant_lang;
    }

    public String getDelivery_lat() {
        return TextUtils.isEmpty(delivery_lat) ? "0.0" : delivery_lat;
    }

    public void setDelivery_lat(String delivery_lat) {
        this.delivery_lat = delivery_lat;
    }

    public String getRestaurant_lat() {
        return TextUtils.isEmpty(restaurant_lat) ? "0.0" : restaurant_lat;
    }

    public void setRestaurant_lat(String restaurant_lat) {
        this.restaurant_lat = restaurant_lat;
    }

    public String getDelivery_lang() {
        return TextUtils.isEmpty(delivery_lang) ? "0.0" : delivery_lang;
    }

    public void setDelivery_lang(String delivery_lang) {
        this.delivery_lang = delivery_lang;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }


    public Integer getOrderPickupStatus() {
        return orderPickupStatus;
    }

    public void setOrderPickupStatus(Integer orderPickupStatus) {
        this.orderPickupStatus = orderPickupStatus;
    }

    public ArrayList<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

}
