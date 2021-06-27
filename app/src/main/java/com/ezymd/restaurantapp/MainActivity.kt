package com.ezymd.restaurantapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var homeViewModel: MainActivityViewModel

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            homeViewModel.contentVisiblity(userInfo!!.configJson)
        }

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
        navView.visibility = View.VISIBLE
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

        homeViewModel.isForceUpgrade.observe(this, Observer {
            if (it && Version.getCurrentVersion(this) < homeViewModel.appUpgradeVersion.value!!) {
                navView.visibility = View.GONE
                upgradeLay.visibility = View.VISIBLE
            }
        })
        upgrade.setOnClickListener {
            UIUtil.clickHandled(it)

            openUpgradeApp()
        }
    }

    private fun openUpgradeApp() {
        val appPackageName = packageName // package name of the app

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
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