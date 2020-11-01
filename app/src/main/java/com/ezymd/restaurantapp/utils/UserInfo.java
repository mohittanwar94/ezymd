package com.ezymd.restaurantapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import java.lang.ref.WeakReference;

public class UserInfo {

    private static final String NOT_HAVE_DEVICE_TOKEN = "can_not_device_token";
    private static UserInfo userInfo;
    private SharedPreferences preferences;
    private WeakReference<Context> mContextWeakReference;
    public MutableLiveData<Boolean> mUserUpdate = new MutableLiveData<>();

    private UserInfo(Context context) {
        if (userInfo == null) {
            this.mContextWeakReference = new WeakReference<>(context.getApplicationContext());
            preferences = context.getApplicationContext().getSharedPreferences("EzymdAppPreferences", Context.MODE_PRIVATE);
        }
    }

    public static UserInfo getInstance(Context mContext) {
        if (userInfo == null) {
            synchronized (UserInfo.class) {
                userInfo = new UserInfo(mContext);
            }
        }
        return userInfo;

    }


    public String getDeviceToken() {
        return preferences.getString("DeviceToken", NOT_HAVE_DEVICE_TOKEN);
    }

    public void setDeviceToken(String token) {
        preferences.edit().putString("DeviceToken", token).apply();
    }

    public String getDeviceID() {
        return preferences.getString("DeviceID", "");
    }

    public void setDeviceID(String id) {
        preferences.edit().putString("DeviceID", id).apply();
    }


    public void setAccessToken(String accessToken) {
        preferences.edit().putString("accessTokenApp", accessToken).apply();
    }


    public int getUserID() {
        return preferences.getInt("user_id", 0);
    }

    public void setUserID(int userid) {
        preferences.edit().putInt("user_id", userid).apply();
    }



    public String getUserName() {
        return preferences.getString("name", "");
    }

    public void setUserName(String name) {
        userInfo.mUserUpdate.postValue(true);
        preferences.edit().putString("name", name).apply();
    }

    public String getPhoneNumber() {
        return preferences.getString("phone_number", "");
    }

    public void setPhoneNumber(String phone_number) {
        userInfo.mUserUpdate.postValue(true);
        preferences.edit().putString("phone_number", phone_number).apply();
    }

    public String getAccessToken() {
        return "Bearer " + preferences.getString("accessTokenApp", "");
    }

    public String getEmail() {
        return preferences.getString("email", "");
    }

    public void setEmail(String email) {
        userInfo.mUserUpdate.postValue(true);
        preferences.edit().putString("email", email).apply();
    }


    public String getProfilePic() {
        return preferences.getString("profilePic", "");
    }

    public void setProfilePic(String pic) {
        userInfo.mUserUpdate.postValue(true);
        preferences.edit().putString("profilePic", pic).apply();
    }



    public void clear() {
        preferences.edit().putString("phone_number", "").apply();
        preferences.edit().putString("email", "").apply();
        preferences.edit().putString("name", "").apply();
        preferences.edit().putString("lat", "0.0").apply();
        preferences.getString("lang", "0.0");
        preferences.edit().putString("profilePic", "").apply();
        preferences.edit().putInt("user_id",0).apply();

    }



    public String getLat() {
        return preferences.getString("lat", "0.0");
    }

    public void setLat(String lat) {
        preferences.edit().putString("lat", lat).apply();
    }

    public String getLang() {
        return preferences.getString("lang", "0.0");
    }

    public void setLang(String lat) {
        preferences.edit().putString("lang", lat).apply();
    }

}