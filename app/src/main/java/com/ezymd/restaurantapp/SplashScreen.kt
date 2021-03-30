package com.ezymd.restaurantapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.ezymd.restaurantapp.login.Login
import com.ezymd.restaurantapp.reviewsList.ReviewsListActivity
import com.ezymd.restaurantapp.utils.SnapLog
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashScreen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotifyManager.getNotificationChannel(
                    getString(R.string.default_notification_channel_id)
                ) != null
            ) {
                mNotifyManager.deleteNotificationChannel(getString(R.string.default_notification_channel_id))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotifyManager.getNotificationChannel(
                    getString(R.string.default_notification_channel_id)
                ) == null
            ) {
                val mChannel = NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    "Ezymd",
                    NotificationManager.IMPORTANCE_HIGH
                )
                val attributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()
                mChannel.enableLights(true)
                mChannel.enableVibration(true)
                mNotifyManager.createNotificationChannel(mChannel)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        // printKeyHash(this)
        if (userInfo!!.userID != 0)
        //startActivity(Intent(this, MainActivity::class.java))
            startActivity(Intent(this, ReviewsListActivity::class.java))
        else
            startActivity(Intent(this, Login::class.java))

        overridePendingTransition(R.anim.left_in, R.anim.left_out)

        this.finish()
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            Log.e("Package Name=", context.applicationContext.packageName)
            for (signature in packageInfo.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                SnapLog.print("Key Hash=" + key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }
}