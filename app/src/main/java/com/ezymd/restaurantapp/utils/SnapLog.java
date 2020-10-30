package com.ezymd.restaurantapp.utils;

import android.util.Log;

import com.ezymd.restaurantapp.BuildConfig;
import com.ezymd.restaurantapp.ServerConfig;


public class SnapLog {
    public static void print(String message) {
            Log.e("com.ezymd", "" + message);
    }
}
