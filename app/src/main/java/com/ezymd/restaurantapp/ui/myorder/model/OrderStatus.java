package com.ezymd.restaurantapp.ui.myorder.model;

public interface OrderStatus {
    int PROCESSING = 1;
    int ORDER_PREPARING = 2;
    int ORDER_ASSIGN_FOR_DELIVERY = 3;
    int ORDER_ACCEPTED = 4;
    int ORDER_ACCEPT_DELIVERY_BOY = 5;
    int DELIVERY_BOY_REACHED_AT_RESTAURANT = 6;
    int ITEMS_PICKED_FROM_RESTAURANT=7;
    int ORDER_COMPLETED=8;
}
