package com.ezymd.restaurantapp.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.coupon.CouponActivity
import com.ezymd.restaurantapp.coupon.model.CoupanModel
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.editprofile.EditProfileActivity
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_cart.*

class CartActivity : BaseActivity() {
    private var discountApplied = 0.0
    var serviceAmount = 0.0
    var deliveryAmount = 0.0
    private var restaurantAdapter: CartAdapter? = null
    private val dataResturant = ArrayList<ItemModel>()

    private val viewModel by lazy {
        ViewModelProvider(this).get(CartViewModel::class.java)
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
        setContentView(R.layout.activity_cart)
        setToolBar()
        setHeaderData()
        setAdapter()
        setGUI()
        setObserver()


    }

    private fun setGUI() {
        pickUp.setOnClickListener {
            UIUtil.clickHandled(it)
            restaurant.isPick = true
            delivery.setTextColor(ContextCompat.getColor(this, R.color.color_002366))
            pickUp.setTextColor(ContextCompat.getColor(this, R.color.white))
            pickUp.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
            delivery.background = ContextCompat.getDrawable(this, R.drawable.pick_up_button_bg)
        }
        delivery.setOnClickListener {
            UIUtil.clickHandled(it)
            restaurant.isPick = false
            pickUp.setTextColor(ContextCompat.getColor(this, R.color.color_002366))
            delivery.setTextColor(ContextCompat.getColor(this, R.color.white))
            delivery.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
            pickUp.background = ContextCompat.getDrawable(this, R.drawable.pick_up_button_bg)
        }
        payButton.setOnClickListener {
            UIUtil.clickHandled(it)
            if (TextUtils.isEmpty(userInfo!!.phoneNumber)) {
                showError(false, "Please Add Your Phone No.", object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        startActivityForResult(
                            Intent(
                                this@CartActivity,
                                EditProfileActivity::class.java
                            ), JSONKeys.OTP_REQUEST
                        )
                        overridePendingTransition(R.anim.left_in, R.anim.left_out)
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
                    }

                })
            } else {
                startConfirmOrder()
            }
        }

        couponCode.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityForResult(
                Intent(
                    this@CartActivity,
                    CouponActivity::class.java
                ).putExtra(JSONKeys.ID, restaurant.id), JSONKeys.LOCATION_REQUEST
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JSONKeys.LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            val coupanModel = data!!.getSerializableExtra(JSONKeys.OBJECT) as CoupanModel
            viewModel.coupanModel.postValue(coupanModel)
        }
    }

    private fun startConfirmOrder() {
        val price = getTotalPrice(EzymdApplication.getInstance().cartData.value!!)
        if (price < restaurant.minOrder.toInt()) {
            val msg = TextUtils.concat(
                "Add ",
                "$" + (restaurant.minOrder.toInt() - price),
                " to reach minimum order amount"
            ).toString()
            showError(false, msg, null)
            return
        }
        val intent = Intent(this, ConfirmOrder::class.java)
        intent.putExtra(JSONKeys.OBJECT, restaurant)
        intent.putExtra(
            JSONKeys.TOTAL_CASH,
            price
        )
        intent.putExtra(JSONKeys.FEE_CHARGES, serviceAmount)
        if (discountApplied > 0.0) {
            intent.putExtra(JSONKeys.PROMO, viewModel.coupanModel.value!!.id)
            intent.putExtra(JSONKeys.DISCOUNT_AMOUNT, discountApplied)
        }
        intent.putExtra(JSONKeys.DELIVERY_CHARGES, deliveryAmount)
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }

    private fun setHeaderData() {
        GlideApp.with(applicationContext)
            .load(restaurant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(ivNotesThumb)

        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.title_cart)
        tvTitle.text = restaurant.name
        foodType.text = restaurant.category

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setObserver() {
        EzymdApplication.getInstance().cartData.observe(this, Observer {
            if (it != null) {
                viewModel.coupanModel.postValue(null)
                discountApplied=0.0
                if (it.size > 0) {
                    notifyAdapter(it)
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("amount", "" + getTotalPrice(it))
                    viewModel.getCharges(baseRequest)

                } else
                    showEmpty()
            }
        })


        viewModel.coupanModel.observe(this, Observer {
            if (it != null) {
                promoLayout.visibility = View.GONE
                promoApply.visibility = View.VISIBLE
                if (it.isFixed == 0) {
                    val price = getTotalPrice(EzymdApplication.getInstance().cartData.value!!)
                    discountApplied = (price * it.discountValue.toDouble()) / 100
                } else {
                    discountApplied = it.discountValue.toDouble()
                }
                promoCharge.text = "$" + discountApplied
                notifyAdapter(EzymdApplication.getInstance().cartData.value!!)
                val baseRequest = BaseRequest(userInfo)
                baseRequest.paramsMap.put("amount", "" +( getTotalPrice(EzymdApplication.getInstance().cartData.value!!)-discountApplied))
                viewModel.getCharges(baseRequest)
            } else {
                promoLayout.visibility = View.VISIBLE
                promoApply.visibility = View.GONE
            }
        })
        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })

        viewModel.mTransactionCharge.observe(this, Observer {

            if (it != null && it.status == ErrorCodes.SUCCESS) {
                serviceAmount = it.transaction_charge
                notifyAdapter(EzymdApplication.getInstance().cartData.value!!)
            } else {
                showError(false, it.message, object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        onBackPressed()
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
                    }

                })
            }
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    private fun getTotalPrice(arrayList: ArrayList<ItemModel>): Double {
        var price = 0.0
        for (itemModel in arrayList) {
            price += (itemModel.price * itemModel.quantity)
        }



        return price
    }

    private fun showEmpty() {
        emptyView.visibility = View.VISIBLE
        content.visibility = View.GONE
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
        restaurantAdapter = CartAdapter(viewModel, this, OnRecyclerView { position, view ->

        }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    override fun onStop() {
        super.onStop()
    }


    private fun notifyAdapter(it: ArrayList<ItemModel>) {
        emptyView.visibility = View.GONE
        content.visibility = View.VISIBLE
        processCartData(it)
        dataResturant.clear()
        dataResturant.addAll(it)
        restaurantAdapter!!.setData(it)

    }

    private fun processCartData(arrayList: ArrayList<ItemModel>) {
        var quantity = 0
        var price = 0
        for (itemModel in arrayList) {
            price += (itemModel.price * itemModel.quantity)
            quantity += itemModel.quantity
        }


        setCartData(quantity, price)

    }

    private fun setCartData(quantity: Int, price: Int) {

        if (quantity == 0 && price == 0) {
            //setEmptyCart()
        }
        setCartDetails(quantity, price)


    }

    private fun setCartDetails(quantityCount: Int, price: Int) {
        runOnUiThread(Runnable {
            serviceCharge.text = TextUtils.concat(
                getString(R.string.dollor),
                "" + String.format("%.2f", serviceAmount)

            )
            SnapLog.print("discountApplied=========="+(discountApplied))
            totalAmount.text = TextUtils.concat(
                getString(R.string.dollor),
                "" + ((serviceAmount + price)-discountApplied)
            )
            subTotal.text = TextUtils.concat(
                getString(R.string.dollor),
                "" + price
            )

            if (price < restaurant.minOrder.toInt()) {
                discount.text =
                    TextUtils.concat(
                        "Add $ ",
                        "" + (restaurant.minOrder.toInt() - price),
                        " to reach minimum order"
                    )
            } else
                discount.text = ""

        })

    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
    }
}