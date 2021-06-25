package com.ezymd.restaurantapp.orderdetails

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.CartActivity
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.orderdetails.adapter.OrderDetailsAdapter
import com.ezymd.restaurantapp.review.ReviewActivity
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.ui.myorder.model.OrderItems
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderStatus
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetailsActivity : BaseActivity() {
    private var updateEmailDialogFragment: RequestInvoiceDialogFragment? = null
    private var restaurantAdapter: OrderDetailsAdapter? = null
    private val dataResturant = ArrayList<OrderItems>()
    private val viewModel by lazy {
        ViewModelProvider(this).get(OrderDetailsViewModel::class.java)
    }


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
        trackOrder.setOnClickListener {
            UIUtil.clickHandled(it)
            makeReorder(item)
        }

        paymentMode.text = getPaymentMode(item.paymentType)
        subTotal.text =
            getString(R.string.dollor) + String.format("%.2f", getTotalPrice(item.orderItems))
        if (!item.discount.equals("0")) {
            discountLay.visibility = View.VISIBLE
            discount.text =
                getString(R.string.dollor) + String.format("%.2f", item.discount.toDouble())
        }
        order_id.text = getString(R.string.orderID) + " #" + item.orderId
        restaurantname.text = item.restaurant?.name
        username.text = userInfo?.userName
        address.text = item.restaurant?.address
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

        setOrderStatus(item.orderStatus)
        leftIcon.setOnClickListener {
            onBackPressed()
        }
        if (!item.deliveryCharges.equals("0"))
            shippingCharge.text =
                getString(R.string.dollor) + String.format("%.2f", item.deliveryCharges.toDouble())
        review.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityForResult(
                Intent(this, ReviewActivity::class.java).putExtra(
                    JSONKeys.OBJECT,
                    item
                ), JSONKeys.LOCATION_REQUEST
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }

        invoice.setOnClickListener {
            UIUtil.clickAlpha(it)
            showInvoiceDialog(it)
        }
    }

    private fun showInvoiceDialog(it: View?) {
        if (supportFragmentManager.findFragmentByTag("fragment_email_update") == null) {
            val fm = supportFragmentManager
            updateEmailDialogFragment = RequestInvoiceDialogFragment.newInstance("", false)
            updateEmailDialogFragment!!.show(fm, "fragment_email_update")
        } else {
            if (!updateEmailDialogFragment!!.isVisible) {
                val fm = supportFragmentManager
                updateEmailDialogFragment?.show(fm, "fragment_email_update")
            }
        }
        updateEmailDialogFragment!!.setOnClickListener(OnEmailUpdate {
            requestInvoiceServer(it)
        })
    }

    private fun requestInvoiceServer(it: String) {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["email"] = it
        viewModel.emailInvoice(baseRequest)
        viewModel.baseResponse.observe(this, Observer {
            showError(it.status == ErrorCodes.SUCCESS, it.message, null)

        })

        viewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {


        })

    }

    private fun getPaymentMode(paymentType: Int): String {
        if (paymentType == PaymentMethodTYPE.COD)
            return getString(R.string.cash_on_delivery)
        else if (paymentType == PaymentMethodTYPE.ONLINE)
            return getString(R.string.card)
        else if (paymentType == PaymentMethodTYPE.GPAY)
            return getString(R.string.googlepay)
        else
            return getString(R.string.wallet)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            EzymdApplication.getInstance().isRefresh.postValue(true)
        }
    }

    private fun makeReorder(orderModel: OrderModel) {
        val list = ArrayList<ItemModel>()
        for (item in orderModel.orderItems) {
            val model = ItemModel()

            model.id = item.id
            model.quantity = item.qty
            model.price = item.price
            model.item = item.item
            model.description = item.description
            val listImage = ArrayList<String>()
            listImage.add(item.image)
            model.image = listImage

            list.add(model)
        }
        EzymdApplication.getInstance().cartData.postValue(list)

        val intent = Intent(this@OrderDetailsActivity, CartActivity::class.java)
        intent.putExtra(JSONKeys.OBJECT, orderModel.restaurant)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)

    }

    private fun setOrderStatus(orderStatus: Int) {
        review.visibility = View.GONE
        feedback.visibility = View.GONE
        val ratingGivent = item.getDelivey_rating().toFloat()
        rating.rating = ratingGivent
        feedback.text = item.feedback
        if (orderStatus == OrderStatus.ORDER_CANCEL) {
            status.text = getString(R.string.your_order_cancel_money_refund)
        } else if (orderStatus == OrderStatus.ORDER_COMPLETED) {
            if (ratingGivent == 0.0f && item.feedback.equals("")) {
                review.visibility = View.VISIBLE
                rating.visibility = View.GONE
            } else {
                rating.visibility = View.VISIBLE

            }
            if (item.feedback != "")
                feedback.visibility = View.VISIBLE
            status.text = getString(R.string.your_order_is_completed)
        } else {
            status.text = getString(R.string.your_order_processing)
            trackOrder.visibility = View.GONE
        }


    }

    override fun onStart() {
        super.onStart()
//        notifyAdapter(item.orderItems)

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


    private fun getTotalPrice(arrayList: ArrayList<OrderItems>): Double {
        var price = 0.0
        for (itemModel in arrayList) {
            price += (itemModel.price * itemModel.qty)
        }



        return price
    }


    private fun setAdapter() {
        dataResturant.addAll(item.orderItems)
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


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}