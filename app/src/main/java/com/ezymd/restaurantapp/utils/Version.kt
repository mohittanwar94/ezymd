package com.ezymd.restaurantapp.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager


object Version {
     fun getCurrentVersion(context: Context): Int {
        val pm: PackageManager = context.packageManager
        val pInfo: PackageInfo?
        try {
            pInfo = pm.getPackageInfo(context.packageName, 0)
        } catch (e1: PackageManager.NameNotFoundException) {
            e1.printStackTrace()
            return -1
        }
        return pInfo!!.versionCode
    }

}