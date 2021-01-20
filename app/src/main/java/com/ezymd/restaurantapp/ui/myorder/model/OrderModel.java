package com.ezymd.restaurantapp.ui.myorder.model;

import android.text.TextUtils;

import com.ezymd.restaurantapp.ui.home.model.Resturant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderModel implements Serializable {
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("restaurant")
    @Expose
    private Resturant restaurant;

    @SerializedName("delivery")
    @Expose
    private DeliveryBoy delivery;

    @SerializedName("feedback")
    @Expose
    private String feedback;

    @SerializedName("payment_type")
    @Expose
    private int paymentType = 1;
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

    @SerializedName("delivery_phone")
    @Expose
    public String delivey_phone;

    @SerializedName("delivery_name")
    @Expose
    public String delivery_name;

    @SerializedName("delivery_pic")
    @Expose
    public String delivery_pic;

    @SerializedName("delivery_rating")
    @Expose
    public String delivey_rating;

    @SerializedName("discount")
    @Expose
    private String discount;


    @SerializedName("delivery_boy_rating")
    @Expose
    public String delivery_boy_rating;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Resturant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Resturant restaurantName) {
        this.restaurant = restaurantName;
    }

    public String getUsername() {
        return TextUtils.isEmpty(username) ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DeliveryBoy getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryBoy delivery) {
        this.delivery = delivery;
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
        return TextUtils.isEmpty(transactionCharges) ? "0.0" : transactionCharges;
    }

    public void setTransactionCharges(String transactionCharges) {
        this.transactionCharges = transactionCharges;
    }

    public String getDelivery_boy_rating() {
        return TextUtils.isEmpty(delivey_rating) ? "0.0" : delivey_rating;
    }

    public void setDelivery_boy_rating(String delivey_rating) {
        this.delivey_rating = delivey_rating;
    }

    public String getDiscount() {
        return TextUtils.isEmpty(discount) ? "0" : discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getFeedback() {
        return TextUtils.isEmpty(feedback) ? "" : feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public String getDeliveryCharges() {
        return TextUtils.isEmpty(deliveryCharges) ? "0" : deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
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


    public String getDelivery_lat() {
        return TextUtils.isEmpty(delivery_lat) ? "" : delivery_lat;
    }

    public void setDelivery_lat(String delivery_lat) {
        this.delivery_lat = delivery_lat;
    }


    public String getDelivery_lang() {
        return TextUtils.isEmpty(delivery_lang) ? "" : delivery_lang;
    }


    public String getDelivey_phone() {
        return TextUtils.isEmpty(delivey_phone) ? "" : delivey_phone;
    }

    public void setDelivey_phone(String delivey_phone) {
        this.delivey_phone = delivey_phone;
    }

    public String getDelivery_name() {
        return TextUtils.isEmpty(delivery_name) ? "" : delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
    }

    public String getDelivery_pic() {
        return TextUtils.isEmpty(delivery_pic) ? "" : delivery_pic;
    }

    public void setDelivery_pic(String delivery_pic) {
        this.delivery_pic = delivery_pic;
    }

    public String getDelivey_rating() {
        return TextUtils.isEmpty(delivey_rating) ? "0.0" : delivey_rating;
    }

    public void setDelivey_rating(String delivey_rating) {
        this.delivey_rating = delivey_rating;
    }

    public void setDelivery_lang(String delivery_lang) {
        this.delivery_lang = delivery_lang;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
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
