package com.ezymd.restaurantapp.utils;

import java.util.HashMap;
import java.util.Map;

public class BaseRequest {
    public String accessToken;
    public Map<String, String> paramsMap;

    public BaseRequest() {
        paramsMap = new HashMap<>();
    }

    public BaseRequest(UserInfo userInfo) {
        accessToken = userInfo.getAccessToken();
        paramsMap = new HashMap<>();
        paramsMap.put("lat", userInfo.getLat());
        paramsMap.put("user_id", "" + userInfo.getUserID());
        paramsMap.put("device_token", userInfo.getDeviceToken());
        paramsMap.put("device_id", userInfo.getDeviceID());
        paramsMap.put("lang", userInfo.getLang());

    }
}
