package com.ezymd.restaurantapp.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.myorder.OrderFragment
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.UIUtil
import com.view.circulartimerview.CircularTimerListener
import com.view.circulartimerview.TimeFormatEnum
import kotlinx.android.synthetic.main.activity_order_success.*


class OrderSuccess : BaseActivity() {
    private var isBackEnable = false

    override fun onBackPressed() {
        if (isBackEnable) {
            startHomeScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)


    }

    override fun onStart() {
        super.onStart()
        setGUI()
    }

    private fun setGUI() {
        if (intent.getBooleanExtra(JSONKeys.IS_PICKUP, false)) {
            successLayout.visibility = View.VISIBLE
            progressLay.visibility = View.GONE
            isBackEnable = true
        } else {
            progress_circular.setPrefix("")
            progress_circular.setCircularTimerListener(object : CircularTimerListener {
                override fun updateDataOnTick(remainingTimeInMs: Long): String? {
                    progress_circular.progress =
                        Math.ceil((remainingTimeInMs / 1000f).toDouble()).toFloat()
                    return (Math.ceil((remainingTimeInMs / 1000f).toDouble()).toInt()).toString()
                }

                override fun onTimerFinished() {
                    progress_circular.setPrefix("")
                    progress_circular.setSuffix("")
                    progress_circular.setText("FINISHED THANKS!")
                    successLayout.visibility = View.VISIBLE
                    progressLay.visibility = View.GONE
                    isBackEnable = true
                }
            }, 60, TimeFormatEnum.SECONDS, 1)

            progress_circular.setMaxValue(60f)
            progress_circular.startTimer()
        }

        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            startHomeScreen()
        }
    }

    private fun startHomeScreen() {
        val i = Intent(this, MainActivity::class.java)
        i.putExtra(JSONKeys.LABEL, OrderFragment::class.java.name)
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