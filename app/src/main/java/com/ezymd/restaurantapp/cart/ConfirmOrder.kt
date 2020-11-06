package com.ezymd.restaurantapp.cart

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.activity_confirm_order.*
import kotlinx.android.synthetic.main.content_scrolling.*
import java.util.*


class ConfirmOrder : BaseActivity() {
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
        setToolBar()
        setHeaderData()
    }

    override fun onStart() {
        super.onStart()
        setGUI()
    }

    private fun setGUI() {
        nowcheckBox.setChecked(true, true)
        nowcheckBox.isClickable=false
        schedulecheckBox.isClickable=false
        viewModel.isNowSelectd.postValue(true)
        viewModel.isNowSelectd.postValue(true)
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
        schedulecheckBox.setChecked(false, false)
        if (!nowcheckBox.isChecked)
            nowcheckBox.setChecked(true, false)
        chooseTime.visibility = View.GONE
        time.visibility = View.GONE


    }

    private fun setScheduleCheckBox() {
        nowcheckBox.setChecked(false, false)
        if (!schedulecheckBox.isChecked)
            schedulecheckBox.setChecked(true, false)
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
                time.text = getString(R.string.delivery_at)+" "+it

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
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }


    override fun onStop() {
        super.onStop()
        viewModel.isNowSelectd.removeObservers(this)
        viewModel.dateSelected.removeObservers(this)
        viewModel.errorRequest.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        EzymdApplication.getInstance().cartData.removeObservers(this)
    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}