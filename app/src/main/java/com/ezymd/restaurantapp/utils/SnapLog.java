package com.ezymd.restaurantapp.utils;

import android.util.Log;

import com.ezymd.restaurantapp.ServerConfig;


public class SnapLog {
    public static void print(String message) {
        if (ServerConfig.IS_TESTING) {
            Log.e("com.ezymd", "" + message);
        }
    }
}
