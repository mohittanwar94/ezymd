package com.ezymd.restaurantapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.ezymd.restaurantapp.ui.myorder.OrderFragment
import com.ezymd.restaurantapp.utils.ConnectivityReceiver
import com.ezymd.restaurantapp.utils.KeepStateNavigator
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED

class MainActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

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

        if (intent.hasExtra(OrderFragment::class.java.name)){
            intent.removeExtra(OrderFragment::class.java.name)
            navView.selectedItemId = R.id.navigation_order
        }

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