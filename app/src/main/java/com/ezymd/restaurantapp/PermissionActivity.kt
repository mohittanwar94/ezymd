package com.ezymd.restaurantapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        setGUI()
    }

    private fun checkLocationPermission() {
        val permission = checkLocationPermissions(object : BaseActivity.PermissionListener {
            override fun result(isGranted: Boolean) {
                if (isGranted)
                    setResult(Activity.RESULT_OK)
                finish()
            }
        })

        if (permission) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()


    }

    override fun onStop() {
        super.onStop()
    }

    private fun setGUI() {
        trackOrder.setOnClickListener {
            UIUtil.clickHandled(it)
            checkLocationPermission()
        }
    }

    override fun onBackPressed() {
    }


}