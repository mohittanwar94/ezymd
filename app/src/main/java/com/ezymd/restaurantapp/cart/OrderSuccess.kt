package com.ezymd.restaurantapp.cart

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.myorder.OrderFragment
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.activity_order_success.*


class OrderSuccess : BaseActivity() {
    private var isBackEnable = false
    private val trackViewModel by lazy {
        ViewModelProvider(this).get(OrderSuccessViewModel::class.java)
    }

    override fun onBackPressed() {
        if (isBackEnable) {
            startHomeScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)
        setObserver()
    }

    private fun setObserver() {
        trackViewModel.showError().observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        trackViewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })


        trackViewModel.firebaseResponse.observe(this, Observer {
            if (it != null) {

            }
        })

        trackViewModel.response.observe(this, Observer {
            if (it != null) {

            }
        })
    }

    override fun onStart() {
        super.onStart()
        setGUI()
    }

    private fun setGUI() {

        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            startHomeScreen()
        }
    }

    private fun startHomeScreen() {
        val i = Intent(this, MainActivity::class.java)
        i.putExtra(JSONKeys.LABEL, OrderFragment::class.java.name)
        i.putExtra(JSONKeys.OBJECT, intent.getSerializableExtra(JSONKeys.OBJECT))
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }


    override fun onResume() {
        super.onResume()
    }


    override fun onStop() {
        super.onStop()
    }


}