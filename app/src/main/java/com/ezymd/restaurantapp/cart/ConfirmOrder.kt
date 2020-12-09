package com.ezymd.restaurantapp.cart

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
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
import com.ezymd.restaurantapp.payment.HostActivity
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OrderCheckoutUtilsModel
import com.ezymd.restaurantapp.utils.ShowDialog
import com.ezymd.restaurantapp.utils.UIUtil
import com.stripe.android.CustomerSession
import kotlinx.android.synthetic.main.activity_confirm_order.*
import java.util.*


class ConfirmOrder : BaseActivity() {
    val checkoutModel = OrderCheckoutUtilsModel()
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
        CustomerSession.initCustomerSession(
            this,
            ExampleEphemeralKeyProvider(ServerConfig.PAYMENT_ACCOUNT_ID)
        )

        setToolBar()
        setHeaderData()
    }

    override fun onStart() {
        super.onStart()
        setGUI()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JSONKeys.LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            val location = data?.getParcelableExtra<LocationModel>(JSONKeys.LOCATION_OBJECT)
            selectAddress.text = location?.location
            checkoutModel.deliveryAddress = selectAddress.text.toString()
        } else if (requestCode == JSONKeys.OTP_REQUEST && resultCode == Activity.RESULT_OK) {
            val deliveryInstructions = data?.getStringExtra(JSONKeys.DESCRIPTION)
            couponCode.text = deliveryInstructions
            checkoutModel.delivery_instruction = couponCode.text.toString()
        }
    }

    private fun setGUI() {
        nowcheckBox.isClickable = false
        schedulecheckBox.isClickable = false

        couponCode.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@ConfirmOrder,
                    AddDeliveryInstruction::class.java
                ),
                JSONKeys.OTP_REQUEST
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
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

        scheduleLayout.setOnClickListener {
            viewModel.isNowSelectd.postValue(false)

        }


        chooseTime.setOnClickListener {
            UIUtil.clickHandled(it)
            showTimePicker()
        }

        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (checkoutModel.deliveryAddress == "") {
                return@setOnClickListener
            }
            checkoutModel.delivery_type = if (viewModel.isNowSelectd.value!!) {
                1
            } else {
                2
            }
            if (checkoutModel.delivery_type == 2)
                checkoutModel.delivery_time = viewModel.dateSelected.value!!
            startActivity(
                Intent(
                    this@ConfirmOrder,
                    HostActivity::class.java
                ).putExtra(JSONKeys.OBJECT, restaurant)
                    .putExtra(JSONKeys.CHEKOUT_OBJECT, checkoutModel)
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
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
                object : TimePickerDialog.OnTimeSetListener {

                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        if (hourOfDay < hour) {
                            ShowDialog(this@ConfirmOrder).disPlayDialog(
                                getString(R.string.pass_time),
                                false,
                                false
                            )
                        } else
                            viewModel.dateSelected.value = "$hourOfDay:$minute"

                    }

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
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.title_confirm_order)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun setObserver() {
        EzymdApplication.getInstance().cartData.observe(this, Observer {

        })
        viewModel.dateSelected.observe(this, Observer {
            if (it != null)
                time.text = getString(R.string.delivery_at) + " " + it

        })
        viewModel.isNowSelectd.observe(this, Observer {
            if (it)
                setNowCheckBoxSelected()
            else
                setScheduleCheckBox()
        })
        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {


        })
    }


    override fun onStop() {
        super.onStop()
        viewModel.isNowSelectd.removeObservers(this)
        viewModel.dateSelected.removeObservers(this)
        viewModel.errorRequest.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        EzymdApplication.getInstance().cartData.removeObservers(this)
    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}