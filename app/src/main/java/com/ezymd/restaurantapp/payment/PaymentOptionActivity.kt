package com.ezymd.restaurantapp.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_checkout.*


class PaymentOptionActivity : BaseActivity() {


    private val viewModel by lazy {
        ViewModelProvider(this).get(PaymentCheckoutViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setToolBar()
        setHeaderData()
        setGUI()
        setObserver()
        viewModel.balanceWallet(BaseRequest(userInfo))
    }

    private fun setGUI() {
        if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.ONLINE) {
            onlineCheckBox.setChecked(true, true)
        } else if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.COD) {
            codCheckBox.setChecked(true, true)
        } else {
            // wallet
        }

        if (intent.getStringExtra(JSONKeys.DELIVERY_CHARGES).equals("Contact less", true)) {
            cod.visibility = View.GONE
        }
        onlineCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked)
                codCheckBox.setChecked(false, false)
        }
        codCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked)
                onlineCheckBox.setChecked(false, false)
        }

        online.setOnClickListener {
            onlineCheckBox.setChecked(true, true)
            codCheckBox.setChecked(false, false)
        }

        cod.setOnClickListener {
            onlineCheckBox.setChecked(false, false)
            codCheckBox.setChecked(true, true)
        }
        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (!codCheckBox.isChecked && !onlineCheckBox.isChecked) {
                showError(false, "please select payment mode", null)
            } else {
                val intent = Intent()
                if (codCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.COD)
                }

                if (onlineCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.ONLINE)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(R.anim.right_in, R.anim.right_out)

            }
        }
    }


    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        viewModel.baseResponse.observe(this, Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                walletbalance.text = getString(R.string.dollor) + "" +  it.data?.total
            }

        })

        viewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }


    private fun setHeaderData() {
        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.color_002366))
        toolbar_layout.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_002366
            )
        )

        toolbar_layout.title = getString(R.string.payment_options)

    }

    private fun setToolBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

}
