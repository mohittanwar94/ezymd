package com.ezymd.restaurantapp.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.payment.ExampleEphemeralKeyProvider
import com.ezymd.restaurantapp.payment.PaymentOptionActivity
import com.ezymd.restaurantapp.payment.StoreActivity
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.utils.*
import com.google.android.gms.wallet.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.stripe.android.*
import com.stripe.android.model.*
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_confirm_order.*
import kotlinx.android.synthetic.main.activity_confirm_order.payButton
import kotlinx.android.synthetic.main.activity_confirm_order.toolbar_layout
import org.json.JSONObject
import java.util.*


class ConfirmOrder : BaseActivity() {
    private var paymentType: Int = 0
    private var totalPrice = 0.0
    private var paymentSessionData: PaymentSessionData? = null
    private var paymentSession: PaymentSession? = null
    val checkoutModel = OrderCheckoutUtilsModel()
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 5000
    private var deliveryIns = "Regular"


    private val stripe: Stripe by lazy {
        Stripe(
            this,
            PaymentConfiguration.getInstance(this).publishableKey,
            PaymentConfiguration.getInstance(this).stripeAccountId,
            ServerConfig.IS_TESTING
        )

    }
    private val googlePayJsonFactory: GooglePayJsonFactory by lazy {
        GooglePayJsonFactory(this)
    }

    private val paymentsClient: PaymentsClient by lazy {
        Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(
                    if (ServerConfig.IS_TESTING) {
                        WalletConstants.ENVIRONMENT_TEST
                    } else {
                        WalletConstants.ENVIRONMENT_PRODUCTION
                    }
                )
                .build()
        )
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(OrderConfirmViewModel::class.java)
    }


    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Resturant
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        if (userInfo!!.customerID == null)
            viewModel.createCustomerStripe(userInfo!!)
        else
            viewModel.isCustomerIDAvailable.postValue(true)
        viewModel.setStripe(stripe)

        setToolBar()
        setHeaderData()
        setObserver()

    }

    override fun onStart() {
        super.onStart()
        setGUI()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JSONKeys.SELECT_PAYMENT && resultCode == Activity.RESULT_OK) {
            val mode = data?.getIntExtra(JSONKeys.PAYMENT_MODE, PaymentMethodTYPE.ONLINE)
            if (mode == PaymentMethodTYPE.ONLINE) {
                paymentType = PaymentMethodTYPE.ONLINE
                paymentMethod.text = getString(R.string.card)
                if (paymentSession == null)
                    startPaymentSession()
                else {
                    paymentSession?.clearPaymentMethod()
                }

            } else {
                paymentMethod.text = getString(R.string.cash_on_delivery)
                paymentType = PaymentMethodTYPE.COD
                checkPayButtomEnableDisable()
                // checkStartPaymentSession()
            }
        } else if (requestCode == JSONKeys.LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            val location = data?.getParcelableExtra<LocationModel>(JSONKeys.LOCATION_OBJECT)
            viewModel.locationSelected.postValue(location)
        } else if (requestCode == JSONKeys.OTP_REQUEST && resultCode == Activity.RESULT_OK) {
            val deliveryInstructions = data?.getStringExtra(JSONKeys.DESCRIPTION)
            couponCode.text = deliveryInstructions
            checkoutModel.delivery_instruction = couponCode.text.toString()
            // checkStartPaymentSession()
        } else if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (data != null) {
                        handleGooglePayResult(data)
                    }
                }
                else -> {
                    resetCheckout()
                }
            }
        } else if (data != null) {
            val isPaymentIntentResult = stripe.onPaymentResult(
                requestCode,
                data,
                object : ApiResultCallback<PaymentIntentResult> {
                    override fun onSuccess(result: PaymentIntentResult) {
                        viewModel.isLoading.postValue(false)
                        processStripeIntent(
                            result.intent,
                            paymentMethod = null
                        )
                    }

                    override fun onError(e: Exception) {
                        viewModel.isLoading.postValue(false)
                        displayError(e.message)
                    }
                }
            )
            if (isPaymentIntentResult) {
                viewModel.isLoading.postValue(true)
            } else {
                val isSetupIntentResult = stripe.onSetupResult(
                    requestCode,
                    data,
                    object : ApiResultCallback<SetupIntentResult> {
                        override fun onSuccess(result: SetupIntentResult) {
                            viewModel.isLoading.postValue(false)
                            processStripeIntent(
                                result.intent,
                                paymentMethod = null
                            )
                        }

                        override fun onError(e: Exception) {
                            viewModel.isLoading.postValue(false)
                            displayError(e.message)
                        }
                    }
                )
                if (!isSetupIntentResult) {
                    paymentSession?.handlePaymentData(requestCode, resultCode, data)
                }


                // paymentSession?.handlePaymentData(requestCode, resultCode, data)
            }
        }
    }

    private fun handleGooglePayResult(data: Intent) {
        val paymentData = PaymentData.getFromIntent(data) ?: return
        val paymentDataJson = JSONObject(paymentData.toJson())

        val paymentMethodCreateParams =
            PaymentMethodCreateParams.createFromGooglePay(paymentDataJson)

        viewModel.isLoading.postValue(true)
        viewModel.createPaymentMethod(paymentMethodCreateParams)
            .observe(
                this,
                { result ->
                    result.fold(
                        onSuccess = {
                            payWithPaymentMethod(it)
                        },
                        onFailure = {
                            displayError(it)
                            resetCheckout()
                        }
                    )
                }
            )
    }

    private fun resetCheckout() {
        paymentSessionData = null
        viewModel.isLoading.postValue(false)

        payButton.isEnabled = false
        checkStartPaymentSession()

        paymentMethod.text = getString(R.string.add_payment_method)
        shippingAddress.text = getString(R.string.add_shipping_details)
    }

    @SuppressLint("SetTextI18n")
    private fun setGUI() {
        contactless.setTextColor(ContextCompat.getColor(this, R.color.black_323232))
        regular.setTextColor(ContextCompat.getColor(this, R.color.black_323232))
        regular.background =
            ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
        contactless.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
        contactless.setOnClickListener {
            UIUtil.clickHandled(it)
            deliveryIns = "Contact less"
            contactless.alpha = 1F
            regular.alpha = 0.3F
            if (paymentType == PaymentMethodTYPE.COD) {
                paymentMethod.text = "Select Payment Method"
                paymentType = 0
                // paymentSession?.clearPaymentMethod()
                checkPayButtomEnableDisable()
            }
        }
        regular.setOnClickListener {
            UIUtil.clickHandled(it)
            deliveryIns = "Regular"
            contactless.alpha = 0.5F
            regular.alpha = 1F
        }
        regular.callOnClick()

        payButton.text =
            getString(R.string.pay) + " " + getString(R.string.dollor) + String.format(
                "%.2f", (intent.getDoubleExtra(
                    JSONKeys.TOTAL_CASH,
                    0.0
                ) + intent.getDoubleExtra(
                    JSONKeys.DELIVERY_CHARGES,
                    0.0
                ) + intent.getDoubleExtra(
                    JSONKeys.FEE_CHARGES,
                    0.0
                ) - intent.getDoubleExtra(
                    JSONKeys.DISCOUNT_AMOUNT,
                    0.0
                )).toDouble()
            )
        if (restaurant.isPick) {
            viewModel.isNowSelectd.postValue(true)
            resturantAddressLay.visibility = View.VISIBLE
            resturantName.text = restaurant.name
            resturantAddress.text = restaurant.address
            nowlayout.visibility = View.GONE
            scheduleLayout.visibility = View.GONE
            delivey_type.text = getString(R.string.pick_up_from)
        } else {
            resturantAddressLay.visibility = View.GONE
        }
        nowcheckBox.isClickable = false
        schedulecheckBox.isClickable = false

        /*couponCode.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@ConfirmOrder,
                    AddDeliveryInstruction::class.java
                ),
                JSONKeys.OTP_REQUEST
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }*/
        selectAddress.setOnClickListener {
            val locationModel = LocationModel()
            locationModel.city = ""
            locationModel.location = "";
            locationModel.lang = userInfo!!.lang.toDouble()
            locationModel.lat = userInfo!!.lat.toDouble()

            startActivityForResult(
                Intent(
                    this@ConfirmOrder,
                    LocationActivity::class.java
                ).putExtra(JSONKeys.LOCATION_OBJECT, locationModel),
                JSONKeys.LOCATION_REQUEST
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
        nowlayout.setOnClickListener {
            viewModel.isNowSelectd.postValue(true)

        }
        shippingAddress.setOnClickListener {
            if (paymentSession != null)
                paymentSession?.presentShippingFlow()
        }

        paymentMethod.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityForResult(
                Intent(
                    this@ConfirmOrder,
                    PaymentOptionActivity::class.java
                ).putExtra(JSONKeys.PAYMENT_TYPE, paymentType)
                    .putExtra(JSONKeys.DELIVERY_CHARGES, deliveryIns),
                JSONKeys.SELECT_PAYMENT
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)

        }
        scheduleLayout.setOnClickListener {
            viewModel.isNowSelectd.postValue(false)

        }


        chooseTime.setOnClickListener {
            UIUtil.clickHandled(it)
            showTimePicker()
        }

        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (paymentType == PaymentMethodTYPE.COD) {
                createCreatePaymentCod()
            } else {
                if (paymentSession == null)
                    return@setOnClickListener
                createPaymentIntent(paymentSessionData!!)
            }

        }

    }

    private fun createPaymentIntent(paymentSessionData: PaymentSessionData) {
        when {
            paymentSessionData.useGooglePay -> payWithGoogle()
            else -> {
                paymentSessionData.paymentMethod?.let { paymentMethod ->
                    payWithPaymentMethod(paymentMethod)
                } ?: displayError("No payment method selected")
            }
        }
    }

    private fun showTimePicker() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker =
            TimePickerDialog(
                this@ConfirmOrder,
                R.style.MyTimePickerDark,
                { view, hourOfDay, minute ->
                    if (hourOfDay < hour) {
                        ShowDialog(this@ConfirmOrder).disPlayDialog(
                            getString(R.string.pass_time),
                            false,
                            false
                        )
                    } else
                        viewModel.dateSelected.value = "$hourOfDay:$minute"
                },
                hour,
                minute,
                true
            )
        mTimePicker.setTitle("Select Delivery Time")
        mTimePicker.show();

    }

    private fun setNowCheckBoxSelected() {
        schedulecheckBox.setChecked(false, true)
        if (!nowcheckBox.isChecked)
            nowcheckBox.setChecked(true, true)
        chooseTime.visibility = View.GONE
        time.visibility = View.GONE


    }

    private fun setScheduleCheckBox() {
        nowcheckBox.setChecked(false, true)
        if (!schedulecheckBox.isChecked)
            schedulecheckBox.setChecked(true, true)
        chooseTime.visibility = View.VISIBLE
        time.visibility = View.VISIBLE


    }

    private fun setHeaderData() {
        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.color_002366))
        toolbar_layout.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_002366
            )
        )
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.title_confirm_order)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setObserver() {
        viewModel.isCustomerIDAvailable.observe(this, Observer {
            if (it) {
                CustomerSession.initCustomerSession(
                    this,
                    ExampleEphemeralKeyProvider(userInfo!!.customerID, userInfo!!.accessToken)
                )

            }
        })

        viewModel.locationSelected.observe(this, Observer {
            val baseRequest = BaseRequest()
            baseRequest.accessToken = userInfo!!.accessToken
            baseRequest.paramsMap.put("lat", "" + it!!.lat)
            baseRequest.paramsMap.put("device_token", userInfo!!.deviceToken)
            baseRequest.paramsMap.put("device_id", userInfo!!.deviceID)
            baseRequest.paramsMap.put("lang", "" + it.lang)
            baseRequest.paramsMap.put("restaurant_id", "" + restaurant.id)

            viewModel.checkAddressLocationValidation(baseRequest)
        })
        viewModel.isAddressValid.observe(this, Observer {
            if (it != null) {
                if (it.status == ErrorCodes.SUCCESS) {
                    selectAddress.text = viewModel.locationSelected.value?.location
                    checkoutModel.deliveryAddress = selectAddress.text.toString()
                    checkPayButtomEnableDisable()

                } else {
                    showError(false, it.message, null)
                }
            }
        })
        EzymdApplication.getInstance().cartData.observe(this, Observer {

        })
        viewModel.baseResponse.observe(this, Observer {

        })
        viewModel.dateSelected.observe(this, Observer {
            if (it != null)
                time.text = getString(R.string.delivery_at) + " " + it
            checkStartPaymentSession()

        })
        viewModel.isNowSelectd.observe(this, Observer {
            if (it)
                setNowCheckBoxSelected()
            else
                setScheduleCheckBox()
            checkStartPaymentSession()
        })
        viewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            SnapLog.print("observer========" + it)
            runOnUiThread(Runnable {
                progressBar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            })


        })
    }

    private fun checkStartPaymentSession() {
        if ((checkoutModel.delivery_type == 2 && viewModel.isNowSelectd.value == null) || checkoutModel.deliveryAddress == "") {
            // showError(false, "Please fill above details first.", null)
            return
        }
        checkoutModel.delivery_type = if (viewModel.isNowSelectd.value!!) {
            1
        } else {
            2
        }
        if (checkoutModel.delivery_type == 2 && viewModel.dateSelected.value != null)
            checkoutModel.delivery_time = viewModel.dateSelected.value!!

        checkPayButtomEnableDisable()

        // startPaymentSession()


    }


    override fun onStop() {
        super.onStop()
    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    private fun startPaymentSession() {
        if (paymentSession == null) {
            paymentSession = PaymentSession(
                this,
                StoreActivity.createPaymentSessionConfig()
            )
            setupPaymentSession()
        }
    }

    private fun setupPaymentSession() {
        paymentSession?.init(
            object : PaymentSession.PaymentSessionListener {
                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    SnapLog.print("onCommunicatingStateChanged" + isCommunicating)


                    // update UI, such as hiding or showing a progress bar
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    displayError(errorMessage)
                    // handle error
                }


                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    SnapLog.print("onPaymentSessionDataChanged")
                    paymentSessionData = data

                    // payButton.isEnabled = data.isPaymentReadyToCharge
                    //  shippingAddress.text = getAddress(paymentSessionData!!)
                    // checkoutModel.shippingAddress = getAddress(paymentSessionData!!)
                    checkPayButtomEnableDisable()
                    if (!data.isPaymentReadyToCharge)
                        paymentSession?.presentPaymentMethodSelection()

                    when {
                        data.useGooglePay -> {
                            updateForGooglePay()
                        }
                        else -> {
                            data.paymentMethod?.let { paymentMethod ->
                                showSelectedMethod(getPaymentMethodDescription(paymentMethod))
                            }
                        }
                    }


                }
            })
    }

    private fun checkPayButtomEnableDisable() {
        if ((checkoutModel.delivery_type == 2 && viewModel.isNowSelectd.value == null) || checkoutModel.deliveryAddress == "" || paymentType == 0 || (checkoutModel.delivery_type == 2 && viewModel.dateSelected.value == null)) {
            // showError(false,  "Please fill above details first.",null)
            payButton.isEnabled = false
            payButton.alpha = 0.5f
            return
        } else {
            payButton.isEnabled = true
            payButton.alpha = 1f
        }

    }

    private fun getAddress(paymentSessionData: PaymentSessionData): String {
        if (TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.line2) && TextUtils.isEmpty(
                paymentSessionData.shippingInformation?.address?.line1
            ) && TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.city) && TextUtils.isEmpty(
                paymentSessionData.shippingInformation?.address?.state
            ) && TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.postalCode) && TextUtils.isEmpty(
                paymentSessionData.shippingInformation?.address?.state
            )
        ) {
            return getString(R.string.add_shipping_details)
        } else {
            val builder = StringBuilder("");
            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.line1)) {
                builder.append(paymentSessionData.shippingInformation?.address?.line1)
            }
            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.line2)) {
                builder.append(", ")
                builder.append(paymentSessionData.shippingInformation?.address?.line2)
            }

            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.city)) {
                builder.append(", ")
                builder.append(paymentSessionData.shippingInformation?.address?.city)
            }


            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.state)) {
                builder.append(", ")
                builder.append(paymentSessionData.shippingInformation?.address?.state)
            }

            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.postalCode)) {
                builder.append(", ")
                builder.append(paymentSessionData.shippingInformation?.address?.postalCode)
            }

            if (!TextUtils.isEmpty(paymentSessionData.shippingInformation?.address?.country)) {
                builder.append(", ")
                builder.append(paymentSessionData.shippingInformation?.address?.country)
            }

            return builder.toString()
        }
    }

    private fun updateForGooglePay() {
        paymentMethod.text =
            getString(R.string.google_pay)
    }

    private fun showSelectedMethod(value: String) {
        paymentMethod.text = value
    }

    private fun getPaymentMethodDescription(paymentMethod: PaymentMethod): String {
        return when (paymentMethod.type) {
            PaymentMethod.Type.Card -> {
                paymentMethod.card?.let {
                    "${it.brand.displayName}-${it.last4}"
                }.orEmpty()
            }
            PaymentMethod.Type.Fpx -> {
                paymentMethod.fpx?.let {
                    "${getDisplayName(it.bank)} (FPX)"
                }.orEmpty()
            }
            else -> ""
        }
    }

    private fun getDisplayName(name: String?): String {
        return (name.orEmpty())
            .split("_")
            .joinToString(separator = " ") { it.capitalize(Locale.ROOT) }
    }

    private fun getJsonObject(online: Int): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", userInfo!!.userName)
        jsonObject.addProperty("email", userInfo!!.email)
        jsonObject.addProperty("phone_no", userInfo!!.phoneNumber)
        jsonObject.addProperty("address", checkoutModel.deliveryAddress)
        jsonObject.addProperty("lat", restaurant.lat)
        jsonObject.addProperty("lang", restaurant.longitude)
        jsonObject.addProperty("payment_type", online)
        jsonObject.addProperty("restaurant_address", restaurant.address)
        jsonObject.addProperty("delivery_lat", viewModel.locationSelected.value?.lat)
        jsonObject.addProperty("delivery_lang", viewModel.locationSelected.value?.lang)

        if (intent.hasExtra(JSONKeys.PROMO)) {
            jsonObject.addProperty("coupon_id", intent.getIntExtra(JSONKeys.PROMO, 0))
            jsonObject.addProperty(
                "discount",
                intent.getDoubleExtra(JSONKeys.DISCOUNT_AMOUNT, 0.0)
            )

        }
        jsonObject.addProperty(
            "order_pickup_status", if (restaurant.isPick) {
                JSONKeys.FROM_RESTAURANT
            } else {
                JSONKeys.DELIVERY
            }
        )
        jsonObject.addProperty(
            "delivery_instruction",
            deliveryIns
        )
        jsonObject.addProperty("shop_id", restaurant.id)
        jsonObject.addProperty("schedule_type", checkoutModel.delivery_type)
        if (checkoutModel.delivery_type == 2) {
            jsonObject.addProperty("schedule_time", checkoutModel.delivery_time)
        }

        val orderItems = JsonArray()
        val listItemModel = EzymdApplication.getInstance().cartData.value

        var price = 0.0
        for (model in listItemModel!!) {
            val jsonObjectModel = JsonObject()
            jsonObjectModel.addProperty("product_id", model.id)
            jsonObjectModel.addProperty("price", model.price)
            jsonObjectModel.addProperty("qty", model.quantity)
            price += (model.price * model.quantity)

            var productVariantids = ""
            var productVariantNames = ""
            for (modifier in model.listModifiers) {
                productVariantids = productVariantids + "," + modifier.id
                productVariantNames = productVariantNames + "," + modifier.title
            }
            if (productVariantids.length > 1)
                productVariantids = productVariantids.substring(1, productVariantids.length)

            if (productVariantNames.length > 1)
                productVariantNames = productVariantNames.substring(1, productVariantNames.length)

            if (productVariantids.length > 1)
                jsonObjectModel.addProperty("product_option_id", productVariantids)

            if (productVariantNames.length > 1)
                jsonObjectModel.addProperty("product_option_name", productVariantNames)

            orderItems.add(jsonObjectModel)
        }
        jsonObject.addProperty(
            "transaction_charges",
            intent.getDoubleExtra(JSONKeys.FEE_CHARGES, 0.0)
        )

        jsonObject.addProperty(
            "delivery_charges",
            intent.getDoubleExtra(JSONKeys.DELIVERY_CHARGES, 0.0)
        )
        totalPrice = String.format(
            "%.2f", (intent.getDoubleExtra(
                JSONKeys.TOTAL_CASH,
                0.0
            ) + intent.getDoubleExtra(JSONKeys.DELIVERY_CHARGES, 0.0) + intent.getDoubleExtra(
                JSONKeys.FEE_CHARGES,
                0.0
            ) - intent.getDoubleExtra(
                JSONKeys.DISCOUNT_AMOUNT,
                0.0
            ))
        ).toDouble()
        jsonObject.addProperty("total", totalPrice)

        paymentSession?.setCartTotal(price.toLong())

        jsonObject.add("orderItems", orderItems)
        return jsonObject
    }

    private fun payWithGoogle() {
        viewModel.isLoading.postValue(true)
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(
                PaymentDataRequest.fromJson(
                    googlePayJsonFactory.createPaymentDataRequest(
                        transactionInfo = GooglePayJsonFactory.TransactionInfo(
                            currencyCode = "USD",
                            totalPrice = totalPrice.toInt(),
                            totalPriceStatus = GooglePayJsonFactory.TransactionInfo.TotalPriceStatus.Final
                        ),
                        merchantInfo = GooglePayJsonFactory.MerchantInfo(
                            merchantName = getString(R.string.app_name)
                        ),
                        shippingAddressParameters = GooglePayJsonFactory.ShippingAddressParameters(
                            isRequired = true,
                            allowedCountryCodes = setOf("US"),
                            phoneNumberRequired = true
                        ),
                        billingAddressParameters = GooglePayJsonFactory.BillingAddressParameters(
                            isRequired = true
                        )
                    ).toString()
                )
            ),
            this@ConfirmOrder,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    private fun payWithPaymentMethod(paymentMethod: PaymentMethod) {
        viewModel.isLoading.postValue(true)
        viewModel.createPaymentIntent(
            createCreatePaymentIntentParams(
                paymentSessionData?.shippingInformation,
                PaymentConfiguration.getInstance(this).stripeAccountId
            ), userInfo!!.accessToken
        ).observe(
            this,
            { result ->
                result.fold(
                    onSuccess = { response ->
                        onStripeIntentClientSecretResponse(response, paymentMethod)
                    },
                    onFailure = ::displayError
                )
            }
        )
    }


    private fun onStripeIntentClientSecretResponse(
        response: JSONObject,
        paymentMethod: PaymentMethod?
    ) {
        if (response.has("success")) {
            val success = response.getBoolean("success")
            if (success) {
                // finishPayment(orderModel)
            } else {
                displayError("Payment failed")
            }
        } else {
            val clientSecret = response.getString("client_secret")
            when {
                clientSecret.startsWith("pi_") ->
                    retrievePaymentIntent(clientSecret, paymentMethod)
                clientSecret.startsWith("seti_") -> {
                }
                //retrieveSetupIntent(clientSecret, paymentMethod)
                else -> throw IllegalArgumentException("Invalid client_secret: $clientSecret")
            }
        }
    }

    private fun retrievePaymentIntent(
        clientSecret: String,
        paymentMethod: PaymentMethod?
    ) {
        viewModel.isLoading.postValue(true)
        viewModel.retrievePaymentIntent(clientSecret)
            .observe(
                this,
                { result ->
                    viewModel.isLoading.postValue(false)
                    result.fold(
                        onSuccess = {
                            processStripeIntent(
                                it,
                                paymentMethod = paymentMethod
                            )
                        },
                        onFailure = ::displayError
                    )
                }
            )
    }

    private fun processStripeIntent(
        stripeIntent: StripeIntent,
        paymentMethod: PaymentMethod?
    ) {
        if (stripeIntent.requiresAction()) {
            stripe.handleNextActionForPayment(this, stripeIntent.clientSecret!!)
        } else if (stripeIntent.requiresConfirmation()) {
            confirmStripeIntent(
                stripeIntent.id!!,
                paymentMethod
            )
        } else if (stripeIntent.status == StripeIntent.Status.Succeeded) {
            if (stripeIntent is PaymentIntent) {
                saveSuccessPayment(stripeIntent)
            }
        } else if (stripeIntent.status == StripeIntent.Status.Canceled) {
            if (stripeIntent is PaymentIntent) {
                saveFailurePayment(stripeIntent)
            }
        } else if (stripeIntent.status == StripeIntent.Status.RequiresPaymentMethod) {
            if (stripeIntent is PaymentIntent) {
                stripe.confirmPayment(
                    this,
                    ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        paymentMethodId = paymentMethod?.id.orEmpty(),
                        clientSecret = requireNotNull(stripeIntent.clientSecret)
                    ),
                    PaymentConfiguration.getInstance(this).stripeAccountId
                )
            }

        } else {
            displayError(
                "Unhandled Payment Intent Status: " + stripeIntent.status.toString()
            )
        }
    }


    private fun createCreatePaymentCod() {
        val baseRequest = BaseRequest(userInfo)
        viewModel.saveCodPaymentInfo(getJsonObject(PaymentMethodTYPE.COD), baseRequest)

        viewModel.savePaymentResponse.observe(this, Observer {
            if (it != null) {
                if (it.status == ErrorCodes.SUCCESS) {
                    finishPayment(it.orderModel)
                } else {
                    showError(false, it.message, null)
                }
            }
        })
    }

    private fun saveSuccessPayment(stripeIntent: PaymentIntent) {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("status", "" + PaymentStatus.SUCCESS)
        baseRequest.paramsMap.put("intent_id", stripeIntent.id)
        baseRequest.paramsMap.put(
            "payment_method",
            getPaymentMethodDescription(stripeIntent.paymentMethod!!)
        )
        viewModel.savePaymentInfo(baseRequest)
        viewModel.savePaymentResponse.observe(this, Observer {
            if (it != null) {
                if (it.status == ErrorCodes.SUCCESS) {
                    finishPayment(it.orderModel)
                } else {
                    showError(false, it.message, null)
                }
            }
        })
    }

    private fun saveFailurePayment(stripeIntent: PaymentIntent) {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("status", "" + PaymentStatus.FAILURE)
        baseRequest.paramsMap.put("intent_id", stripeIntent.id)
        baseRequest.paramsMap.put(
            "payment_method",
            getPaymentMethodDescription(stripeIntent.paymentMethod!!)
        )
        viewModel.savePaymentInfo(baseRequest)
        viewModel.savePaymentResponse.observe(this, Observer {
            if (it != null) {
                if (it.status == ErrorCodes.SUCCESS) {
                    finishPayment(it.orderModel)
                } else {
                    showError(false, it.message, null)
                }
            }
        })
    }

    private fun confirmStripeIntent(
        stripeIntentId: String,
        paymentMethod: PaymentMethod?
    ) {
        val params = mapOf(
            "payment_intent_id" to stripeIntentId
        ).plus(
            PaymentConfiguration.getInstance(this).stripeAccountId.let {
                mapOf("stripe_account" to it)
            }.orEmpty()
        )

        viewModel.isLoading.postValue(true)
        viewModel.confirmStripeIntent(
            params
        ).observe(
            this,
            { result ->
                viewModel.isLoading.postValue(false)
                result.fold(
                    onSuccess = {
                        onStripeIntentClientSecretResponse(
                            it,
                            paymentMethod
                        )
                    },
                    onFailure = ::displayError
                )
            }
        )
    }

    private fun createCreatePaymentIntentParams(
        shippingInformation: ShippingInformation?,
        stripeAccountId: String?
    ): Map<String, Any> {
        return mapOf(
            "country" to "US",
            "customer_id" to userInfo!!.customerID,
            "data" to getJsonObject(PaymentMethodTYPE.ONLINE)
        )/*.plus(
            shippingInformation?.let {
                mapOf("shipping" to it.toParamMap())
            }.orEmpty()
        )*/.plus(
            stripeAccountId?.let {
                mapOf("stripe_account" to it)
            }.orEmpty()
        )
    }

    private fun displayError(throwable: Throwable) = displayError(throwable.message)

    private fun displayError(errorMessage: String?) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(errorMessage)
            .setNeutralButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun finishPayment(orderModel: OrderModel) {

        val intent = Intent(this@ConfirmOrder, OrderSuccess::class.java)
        intent.putExtra(JSONKeys.IS_PICKUP, true)
        intent.putExtra(JSONKeys.OBJECT, orderModel)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
        finishWithResult(
            100
        )

    }


    private fun finishWithResult(i: Int) {
        // success screen
        paymentSession?.onCompleted()

    }

}