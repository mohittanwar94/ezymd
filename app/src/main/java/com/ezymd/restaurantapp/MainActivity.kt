package com.ezymd.restaurantapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.utils.ConnectivityReceiver
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.KeepStateNavigator
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED

class MainActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var referrerClient: InstallReferrerClient

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navController = findNavController(R.id.nav_host_fragment)

        // get fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!

        // setup custom navigator
        val navigator =
            KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_host_fragment)
        navController.navigatorProvider += navigator

        // set navigation graph
        navController.setGraph(R.navigation.mobile_navigation)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LABEL_VISIBILITY_LABELED
        SnapLog.print("onCreate")

        if (intent.hasExtra(JSONKeys.LABEL)) {
            intent.removeExtra(JSONKeys.LABEL)
            navView.selectedItemId = R.id.navigation_order
            if (intent.hasExtra(JSONKeys.OBJECT)) {
                val startIntent = Intent(this@MainActivity, TrackerActivity::class.java)
                startIntent.putExtra(JSONKeys.OBJECT, intent.getSerializableExtra(JSONKeys.OBJECT))
                startActivity(startIntent)
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }
        }

        initTracking()
    }


    private fun initTracking() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        obtainReferrerDetails()
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun obtainReferrerDetails() {
        val response: ReferrerDetails = referrerClient.installReferrer
        SnapLog.print("response - $response")
        val referrerUrl: String = response.installReferrer
        Log.d("referrerUrl - ", referrerUrl)
        val referrerClickTime: Long = response.referrerClickTimestampSeconds
        Log.d("referrerClickTime - ", referrerClickTime.toString())
        val appInstallTime: Long = response.installBeginTimestampSeconds
        Log.d("appInstallTime - ", appInstallTime.toString())
        //val instantExperienceLaunched: Boolean = response.googlePlayInstantParam
        // Log.d("instantExperienceLaunch", instantExperienceLaunched.toString())

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        SnapLog.print("onNewIntent")
    }

    override fun onResume() {
        super.onResume()
        EzymdApplication.getInstance().setConnectivityListener(this@MainActivity);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            noNetworkScreen()
        }
    }

    private fun noNetworkScreen() {

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}