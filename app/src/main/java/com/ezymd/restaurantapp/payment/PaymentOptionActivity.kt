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
import com.ezymd.restaurantapp.BuildConfig
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.payment.gpay.PaymentsUtil
import com.ezymd.restaurantapp.utils.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import kotlinx.android.synthetic.main.activity_checkout.*


class PaymentOptionActivity : BaseActivity() {
    private var isLoaded = false

    private val viewModel by lazy {
        ViewModelProvider(this).get(PaymentCheckoutViewModel::class.java)
    }

    private val paymentsClient: PaymentsClient by lazy {
        Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(
                    BuildConfig.WALLET_ENVIRONMENT.toInt()
                )
                .build()
        )
    }

    private var wAmount = 0.0
    private var tAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        wAmount = intent.getDoubleExtra(JSONKeys.WALLET_AMOUNT, 0.0)
        tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
        wallet.visibility=View.GONE
        setToolBar()
        setHeaderData()
        setGUI()
        setObserver()
        viewModel.balanceWallet(BaseRequest(userInfo))

    }

    @SuppressLint("SetTextI18n")
    private fun setGUI() {
        /*if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.ONLINE) {
            onlineCheckBox.setChecked(true, true)
        } else*/ if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.COD) {
            codCheckBox.setChecked(true, true)
        } else if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.WALLET) {
            walletCheckBox.setChecked(true, true)
            // wallet
        }/* else if (intent.getIntExtra(JSONKeys.PAYMENT_TYPE, 1) == PaymentMethodTYPE.GPAY) {
            gpayCheckBox.setChecked(true, true)
            // wallet
        }*/

        if (intent.getStringExtra(JSONKeys.DELIVERY_CHARGES).equals("Contact less", true)) {
            cod.visibility = View.GONE
        }
       /* onlineCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                wAmount = 0.0
                tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
                instructions.text = ""
                codCheckBox.setChecked(false, false)
                walletCheckBox.setChecked(false, false)
                gpayCheckBox.setChecked(false, false)
            }
        }*/

      /*  gpayCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                wAmount = 0.0
                tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
                instructions.text = ""
                codCheckBox.setChecked(false, false)
                walletCheckBox.setChecked(false, false)
                onlineCheckBox.setChecked(false, false)
            }
        }
*/
        walletCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
            if (isChecked) {
                codCheckBox.setChecked(false, false)
               // onlineCheckBox.setChecked(false, false)
                //gpayCheckBox.setChecked(false, false)
                if (isLoaded) {
                    if (tAmount > viewModel.baseResponse.value!!.data?.total!!.toDouble()) {
                        instructions.text =
                            getString(R.string.dollor) + "" + viewModel.baseResponse.value!!.data?.total + " will be debit from your wallet & remaining will be done through online payment mode"
                        wAmount = viewModel.baseResponse.value!!.data?.total!!.toDouble()
                        tAmount -= wAmount
                    } else if (tAmount == viewModel.baseResponse.value!!.data?.total!!.toDouble() || tAmount < viewModel.baseResponse.value!!.data?.total!!.toDouble()) {
                        instructions.text = ""
                        wAmount = tAmount
                        tAmount = 0.0
                    }
                } else {
                    codCheckBox.setChecked(false, false)
                   // onlineCheckBox.setChecked(false, false)
                    walletCheckBox.setChecked(false, false)
                  //  gpayCheckBox.setChecked(false, false)
                }

            }
        }
        codCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                wAmount = 0.0
                tAmount = intent.getDoubleExtra(JSONKeys.AMOUNT, 0.0)
                instructions.text = ""
                walletCheckBox.setChecked(false, false)
               // onlineCheckBox.setChecked(false, false)
              //  gpayCheckBox.setChecked(false, false)
            }
        }

       /* online.setOnClickListener {
            onlineCheckBox.setChecked(true, false)
        }
*/
        cod.setOnClickListener {
            codCheckBox.setChecked(true, false)
        }

        wallet.setOnClickListener {
            walletCheckBox.setChecked(true, false)


        }

       /* gpay.setOnClickListener {
            gpayCheckBox.setChecked(true, false)


        }
       */ payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (!codCheckBox.isChecked && /*!onlineCheckBox.isChecked &&*/ !walletCheckBox.isChecked /*&& !gpayCheckBox.isChecked*/) {
                showError(false, "please select payment mode", null)
            } else {
                val intent = Intent()
                intent.putExtra(JSONKeys.AMOUNT, tAmount)
                intent.putExtra(JSONKeys.WALLET_AMOUNT, wAmount)
                if (codCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.COD)
                }

              /*  if (onlineCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.ONLINE)
                }

                if (gpayCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.GPAY)
                }
*/
                if (walletCheckBox.isChecked) {
                    intent.putExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.WALLET)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(R.anim.right_in, R.anim.right_out)

            }
        }


        possiblyShowGooglePayButton()

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


    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                // Process error
                SnapLog.print("isReadyToPay failed" + exception)
            }
        }
    }

    private fun setGooglePayAvailable(available: Boolean) {
     /*   if (available) {
            gpay.visibility = View.VISIBLE
        } else {
            gpay.visibility = View.GONE
        }*/
    }

}
