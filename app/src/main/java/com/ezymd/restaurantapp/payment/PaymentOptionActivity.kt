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
    private var isLoaded = false

    private val viewModel by lazy {
        ViewModelProvider(this).get(PaymentCheckoutViewModel::class.java)
    }


    private var wAmount = 0.0
    private var tAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        wAmount = intent.getDoubleExtra(JSONKeys.WALLET_AMOUNT, 0.0)
        tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
        setToolBar()
        setHeaderData()
        setGUI()
        setObserver()
        viewModel.balanceWallet(BaseRequest(userInfo))

    }

    @SuppressLint("SetTextI18n")
    private fun setGUI() {
        if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.ONLINE) {
            onlineCheckBox.setChecked(true, true)
        } else if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.COD) {
            codCheckBox.setChecked(true, true)
        } else if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.WALLET) {
            walletCheckBox.setChecked(true, true)
            // wallet
        }

        if (intent.getStringExtra(JSONKeys.DELIVERY_CHARGES).equals("Contact less", true)) {
            cod.visibility = View.GONE
        }
        onlineCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                wAmount = 0.0
                tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
                instructions.text = ""
                codCheckBox.setChecked(false, false)
                walletCheckBox.setChecked(false, false)
            }
        }

        walletCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
            if (isChecked) {
                codCheckBox.setChecked(false, false)
                onlineCheckBox.setChecked(false, false)

                if (isLoaded) {
                    if (tAmount > viewModel.baseResponse.value!!.data?.total!!.toDouble()) {
                        instructions.text =
                            getString(R.string.dollor) + "" + viewModel.baseResponse.value!!.data?.total + " will be debit from your wallet & remaining will be done through online payment mode"
                        wAmount = 20.0//viewModel.baseResponse.value!!.data?.total!!.toDouble()
                        tAmount -= wAmount
                    }
                } else {
                    codCheckBox.setChecked(false, false)
                    onlineCheckBox.setChecked(false, false)
                    walletCheckBox.setChecked(false, false)
                }

            }
        }
        codCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                wAmount = 0.0
                tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
                instructions.text = ""
                walletCheckBox.setChecked(false, false)
                onlineCheckBox.setChecked(false, false)
            }
        }

        online.setOnClickListener {
            onlineCheckBox.setChecked(true, false)
        }

        cod.setOnClickListener {
            codCheckBox.setChecked(true, false)
        }

        wallet.setOnClickListener {
            walletCheckBox.setChecked(true, false)


        }
        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (!codCheckBox.isChecked && !onlineCheckBox.isChecked && !walletCheckBox.isChecked) {
                showError(false, "please select payment mode", null)
            } else {
                val intent = Intent()
                intent.putExtra(JSONKeys.AMOUNT, tAmount)
                intent.putExtra(JSONKeys.WALLET_AMOUNT, wAmount)
                if (codCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.COD)
                }

                if (onlineCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.ONLINE)
                }

                if (walletCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.WALLET)
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
                isLoaded = true
                walletbalance.text = getString(R.string.dollor) + "" + it.data?.total
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
