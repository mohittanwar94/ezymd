package com.ezymd.restaurantapp.orderdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.orderdetails.adapter.OrderDetailsAdapter
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.ui.myorder.model.OrderItems
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
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
        order_id.text = "#" + item.orderId
        name.text = item.restaurantName

        totalAmount.text = getString(R.string.dollor) + item.total
        created.text = TimeUtils.getReadableDate(item.created)
        paymentID.text = item.paymentId
        deliveryInstruction.text = item.deliveryInstruction
        address.text = item.address
        if (item.scheduleType == 2) {
            scheduleAt.text = item.scheduleTime
        } else {
            scheduleAt.text = getString(R.string.now)
        }
        if (item.orderPickupStatus.equals(JSONKeys.FROM_RESTAURANT)) {
            trackOrder.visibility = View.GONE
            //JSONKeys.FROM_RESTAURANT
        } /*else {
            JSONKeys.DELIVERY
        }*/


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

        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = "#" + item.orderId

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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