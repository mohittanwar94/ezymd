package com.ezymd.restaurantapp.utils;

public interface PaymentStatus {
    int INITIATE = 1;
    int SETTLEMENT = 2;
    int FAILURE = 4;
    int REFUND = 6;
    int SUCCESS = 5;


}
