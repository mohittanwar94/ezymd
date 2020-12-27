package com.ezymd.restaurantapp.ui.myorder.model;

public interface OrderStatus {
    int PROCESSING = 1;
    int ORDER_ACCEPTED = 2;
    int ORDER_ACCEPT_DELIVERY_BOY = 3;
    int DELIVERY_BOY_REACHED_AT_RESTAURANT = 4;
    int ITEMS_PICKED_FROM_RESTAURANT = 5;
    int ORDER_COMPLETED = 6;
}
