package com.ezymd.restaurantapp.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OrderCheckoutUtilsModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.activity_checkout.*
import java.lang.ref.WeakReference


class CheckoutActivity : BaseActivity() {

    /**
     * This example collects card payments, implementing the guide here: https://stripe.com/docs/payments/accept-a-payment#android
     *
     * To run this app, follow the steps here: https://github.com/stripe-samples/accept-a-card-payment#how-to-run-locally
     */
    // 10.0.2.2 is the Android emulator's alias to localhost
    private lateinit var publishableKey: String
    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe

    private val viewModel by lazy {
        ViewModelProvider(this).get(PaymentCheckoutViewModel::class.java)
    }

    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Resturant
    }

    private val checkOutObject by lazy {
        intent.getSerializableExtra(JSONKeys.CHEKOUT_OBJECT) as OrderCheckoutUtilsModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setToolBar()
        setHeaderData()
        startCheckout()

    }


    override fun onStart() {
        super.onStart()
        setObserver()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.errorRequest.removeObservers(this)
        viewModel.baseResponse.removeObservers(this)
        viewModel.isLoading.removeObservers(this)

    }

    private fun setObserver() {
        viewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })

        viewModel.baseResponse.observe(this, Observer {

        })
        viewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun displayAlert(
        activity: Activity,
        title: String,
        message: String,
        restartDemo: Boolean = false
    ) {
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
            if (restartDemo) {
                builder.setPositiveButton("Try Again") { _, _ ->
                    val cardInputWidget =
                        findViewById<CardInputWidget>(R.id.cardInputWidget)
                    cardInputWidget.clear()
                    startCheckout()
                }
            } else {
                builder.setPositiveButton("Ok", null)
            }
            builder
                .create()
                .show()
        }
    }

    private fun startCheckout() {
        val jsonObject = getJsonObject()
        viewModel.checkout(jsonObject, BaseRequest(userInfo!!))
        /*   val weakActivity = WeakReference<Activity>(this)
           // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
           val mediaType = "application/json; charset=utf-8".toMediaType()
           val requestJson = """
               {
                   "currency":"usd",
                   "items": [
                       {"id":"photo_subscription"}
                   ]
               }
               """
           val body = requestJson.toRequestBody(mediaType)
           val request = Request.Builder()
               .url(backendUrl + ServerConfig.CREATE_ORDER)
               .post(body)
               .build()
           httpClient.newCall(request)
               .enqueue(object : Callback {
                   override fun onFailure(call: Call, e: IOException) {
                       weakActivity.get()?.let { activity ->
                           displayAlert(activity, "Failed to load page", "Error: $e")
                       }
                   }

                   override fun onResponse(call: Call, response: Response) {
                       if (!response.isSuccessful) {
                           weakActivity.get()?.let { activity ->
                               displayAlert(
                                   activity,
                                   "Failed to load page",
                                   "Error: $response"
                               )
                           }
                       } else {
                           val responseData = response.body?.string()
                           val responseJson =
                               responseData?.let { JSONObject(it) } ?: JSONObject()

                           // The response from the server includes the Stripe publishable key and
                           // PaymentIntent details.
                           // For added security, our sample app gets the publishable key
                           // from the server.
                           publishableKey = responseJson.getString("publishableKey")
                           paymentIntentClientSecret = responseJson.getString("clientSecret")

                           // Configure the SDK with your Stripe publishable key so that it can make
                           // requests to the Stripe API
                           stripe = Stripe(applicationContext, publishableKey)
                       }
                   }
               })
   */
        // Hook up the pay button to the card widget and stripe instance
        val payButton: Button = findViewById(R.id.payButton)
        payButton.setOnClickListener {
            val cardInputWidget =
                findViewById<CardInputWidget>(R.id.cardInputWidget)
            cardInputWidget.paymentMethodCreateParams?.let { params ->
                val confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
                stripe.confirmPayment(this, confirmParams)
            }
        }
    }

    private fun getJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", userInfo!!.userName)
        jsonObject.addProperty("email", userInfo!!.email)
        jsonObject.addProperty("phone_no", userInfo!!.phoneNumber)
        jsonObject.addProperty("address", checkOutObject.deliveryAddress)
        jsonObject.addProperty("delivery_instruction", checkOutObject.delivery_instruction)
        jsonObject.addProperty("restaurant_id", restaurant.id)
        jsonObject.addProperty("schedule_type", checkOutObject.delivery_type)
        if (checkOutObject.delivery_type == 2) {
            jsonObject.addProperty("schedule_time", checkOutObject.delivery_time)
        }

        val orderItems = JsonArray()
        val listItemModel = EzymdApplication.getInstance().cartData.value

        var price = 0
        for (model in listItemModel!!) {
            val jsonObjectModel = JsonObject()
            jsonObjectModel.addProperty("food_id", model.id)
            jsonObjectModel.addProperty("price", model.price)
            jsonObjectModel.addProperty("qty", model.quantity)
            price += (model.price * model.quantity)
            orderItems.add(jsonObjectModel)
        }
        jsonObject.addProperty("total", price)
        jsonObject.add("orderItems", orderItems)
        return jsonObject
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val weakActivity = WeakReference<Activity>(this)

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                val status = paymentIntent.status
                if (status == StripeIntent.Status.Succeeded) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    weakActivity.get()?.let { activity ->
                        displayAlert(
                            activity,
                            "Payment succeeded",
                            gson.toJson(paymentIntent),
                            restartDemo = true
                        )
                    }
                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                    weakActivity.get()?.let { activity ->
                        displayAlert(
                            activity,
                            "Payment failed",
                            paymentIntent.lastPaymentError?.message.orEmpty()
                        )
                    }
                }
            }

            override fun onError(e: Exception) {
                weakActivity.get()?.let { activity ->
                    displayAlert(
                        activity,
                        "Payment failed",
                        e.toString()
                    )
                }
            }
        })
    }

    private fun setHeaderData() {
        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.title_checkout)

    }

    private fun setToolBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
