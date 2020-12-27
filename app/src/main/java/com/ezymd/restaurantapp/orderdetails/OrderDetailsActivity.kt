package com.ezymd.restaurantapp.orderdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.orderdetails.adapter.OrderDetailsAdapter
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.ui.myorder.model.OrderItems
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderStatus
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.TimeUtils
import com.ezymd.restaurantapp.utils.VerticalSpacesItemDecoration
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailsActivity : BaseActivity() {
    private var restaurantAdapter: OrderDetailsAdapter? = null
    private val dataResturant = ArrayList<OrderItems>()


    private val item by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as OrderModel
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        setToolBar()
        setHeaderData()
        setAdapter()
        setGUI()


    }

    @SuppressLint("SetTextI18n")
    private fun setGUI() {
        order_id.text = getString(R.string.orderID) + " #" + item.orderId
        restaurantname.text = item.restaurantName
        username.text = userInfo?.userName
        address.text = item.restaurantAddress
        order_info.text =
            TimeUtils.getReadableDate(item.created) + " | " + item.orderItems.size + " items | " + getString(
                R.string.dollor
            ) + item.total
        totalAmount.text = getString(R.string.dollor) + item.total
        deliveryInstruction.text = item.deliveryInstruction
        userAddress.text = item.address
        if (item.scheduleType == 2) {
            scheduleAt.text = item.scheduleTime
        } else {
            scheduleAt.text = getString(R.string.now)
        }
        serviceCharge.text = getString(R.string.dollor) + "" + item.transactionCharges

        setOrderStatus(item.orderStatus)
        leftIcon.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setOrderStatus(orderStatus: Int) {
        review.visibility = View.GONE
        feedback.visibility = View.GONE
        val ratingGivent = item.getDelivey_rating().toFloat()
        rating.rating = ratingGivent
        feedback.text = item.feedback
        if (orderStatus != OrderStatus.ORDER_COMPLETED) {
            status.text = getString(R.string.your_order_processing)
        } else {
            if (ratingGivent == 0.0f && item.feedback.equals("")) {
                review.visibility = View.VISIBLE
                rating.visibility = View.GONE
            }else{
                rating.visibility = View.VISIBLE

            }
            if (item.feedback != "")
                feedback.visibility = View.VISIBLE
            status.text = getString(R.string.your_order_is_completed)
        }


    }

    override fun onStart() {
        super.onStart()
        notifyAdapter(item.orderItems)

    }


    private fun startTrackOrder() {
        val intent = Intent(this, TrackerActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }

    private fun setHeaderData() {

        order_id.text = "#" + item.orderId

    }


    override fun onResume() {
        super.onResume()
    }


    private fun getTotalPrice(arrayList: ArrayList<ItemModel>): Double {
        var price = 0.0
        for (itemModel in arrayList) {
            price += (itemModel.price * itemModel.quantity)
        }



        return price
    }


    private fun setAdapter() {
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._13sdp
                ))
            )
        )
        restaurantAdapter = OrderDetailsAdapter(this, OnRecyclerView { position, view ->

        }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    override fun onStop() {
        super.onStop()
    }


    private fun notifyAdapter(it: ArrayList<OrderItems>) {
        dataResturant.clear()
        dataResturant.addAll(it)
        restaurantAdapter!!.setData(it)

    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}