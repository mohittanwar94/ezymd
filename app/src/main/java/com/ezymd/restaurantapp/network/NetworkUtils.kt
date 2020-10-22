package com.ezymd.restaurantapp.network

import android.content.Context
import android.os.Build
import java.util.*

object NetworkUtils {
    fun addCommonParameters(
        map: HashMap<String?, String?>,
        mContext: Context?
    ): HashMap<String?, String?> {
        map["data[device_type]"] = "android"
        map["data[device_name]"] = Build.MANUFACTURER + " " + Build.MODEL
        map["data[os_version]"] = Build.VERSION.SDK_INT.toString() + ""
        return map
    }

    fun addCommonParameters(map: HashMap<String?, String?>): HashMap<String?, String?> {
        map["data[device_type]"] = "android"
        map["data[device_name]"] = Build.MANUFACTURER + " " + Build.MODEL
        map["data[os_version]"] = Build.VERSION.SDK_INT.toString() + ""
        return map
    }
}