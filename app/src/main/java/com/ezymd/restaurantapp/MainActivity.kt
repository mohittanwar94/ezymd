package com.ezymd.restaurantapp

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ezymd.restaurantapp.utils.ConnectivityReceiver
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED

class MainActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LABEL_VISIBILITY_LABELED

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
}