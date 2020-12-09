package com.ezymd.restaurantapp.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.ezymd.restaurantapp.R
import com.stripe.android.PaymentSession
import com.stripe.android.PaymentSession.PaymentSessionListener
import com.stripe.android.PaymentSessionData
import com.stripe.android.model.ShippingInformation
import kotlinx.android.synthetic.main.activity_payment.*

class HostActivity : ComponentActivity() {
    private var paymentSession: PaymentSession? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        paymentSession = PaymentSession(
            this,
            StoreActivity.createPaymentSessionConfig()
        )
        setupPaymentSession()
    }

    private fun setupPaymentSession() {
        paymentSession!!.init(
            object : PaymentSessionListener {
                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    progress_bar.visibility = if (isCommunicating) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    // update UI, such as hiding or showing a progress bar
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    // handle error
                }

                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    val paymentMethod = data.paymentMethod
                    if (data.useGooglePay) {
                        // customer intends to pay with Google Pay
                    } else {
                        if (paymentMethod != null) {
                            // Display information about the selected payment method
                        }
                    }
                    paymentSession?.presentPaymentMethodSelection()
                    // Update your UI here with other data
                    if (data.isPaymentReadyToCharge) {
                        // Use the data to complete your charge - see below.
                    }

                }
            }
        )
        button_confirm_payment.setEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            paymentSession!!.handlePaymentData(requestCode, resultCode, data)
        }
    }

    // optionally specify default shipping address
    private val defaultShippingInfo: ShippingInformation
        private get() =// optionally specify default shipping address
            ShippingInformation()
}