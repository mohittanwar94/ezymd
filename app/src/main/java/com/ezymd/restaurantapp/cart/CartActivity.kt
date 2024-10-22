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
import kotlinx.android.synthetic.main.activity_cart.content
import kotlinx.android.synthetic.main.activity_cart.payButton
import kotlinx.android.synthetic.main.activity_cart.promoLayout
import kotlinx.android.synthetic.main.activity_cart.toolbar_layout
import kotlinx.android.synthetic.main.activity_confirm_order.*

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
        delivery.setTextColor(ContextCompat.getColor(this, R.color.color_002366))
        pickUp.setTextColor(ContextCompat.getColor(this, R.color.color_002366))
        pickUp.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
        delivery.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
        pickUp.setOnClickListener {
            UIUtil.clickHandled(it)
            restaurant.isPick = true
            delivery.alpha=0.5F
            pickUp.alpha=1F
        }
        delivery.setOnClickListener {
            UIUtil.clickHandled(it)
            restaurant.isPick = false
            pickUp.alpha=0.5F
            delivery.alpha=1F
        }
        delivery.callOnClick()
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
                userInfo?.currency + (restaurant.minOrder.toInt() - price),
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
        intent.putExtra(JSONKeys.FEE_CHARGES, String.format("%.2f",serviceAmount).toDouble())
        if (discountApplied > 0.0) {
            intent.putExtra(JSONKeys.PROMO, viewModel.coupanModel.value!!.id)
            intent.putExtra(JSONKeys.DISCOUNT_AMOUNT, discountApplied)
        }
        intent.putExtra(JSONKeys.DELIVERY_CHARGES,  String.format("%.2f",deliveryAmount).toDouble())
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }

    private fun setHeaderData() {
        GlideApp.with(applicationContext)
            .load(restaurant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(ivNotesThumb)

        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setExpandedTitleColor(ContextCompat.getColor(this,R.color.color_1e222a))
        toolbar_layout.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.color_1e222a))
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
                   /* val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("amount", "" + getTotalPrice(it))
                    viewModel.getCharges(baseRequest)*/

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
                    discountApplied= String.format("%.2f",discountApplied).toDouble()
                } else {
                    discountApplied = it.discountValue.toDouble()
                    discountApplied= String.format("%.2f",discountApplied).toDouble()
                }
                promoCharge.text = userInfo?.currency + discountApplied
                notifyAdapter(EzymdApplication.getInstance().cartData.value!!)
               /* val baseRequest = BaseRequest(userInfo)
                baseRequest.paramsMap.put("amount", "" +( getTotalPrice(EzymdApplication.getInstance().cartData.value!!)-discountApplied))
                viewModel.getCharges(baseRequest)*/
            } else {
                promoLayout.visibility = View.VISIBLE
                promoApply.visibility = View.GONE
            }
        })
        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })

        viewModel.mTransactionCharge.observe(this, Observer {

           /* if (it != null && it.status == ErrorCodes.SUCCESS) {
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

                })*/
            })
        viewModel.isLoading.observe(this, Observer {
          //  progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    private fun getTotalPrice(arrayList: ArrayList<ItemModel>): Double {
        val calc = CalculationUtils().processCartData(arrayList)
        return String.format("%.2f",calc.second).toDouble()
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
        val calc = CalculationUtils().processCartData(arrayList)
        setCartData(calc.first, calc.second)
    }

    private fun setCartData(quantity: Int, price: Double) {

        if (quantity == 0 && price == 0.0) {
            //setEmptyCart()
        }
        setCartDetails(quantity, price)


    }

    private fun setCartDetails(quantityCount: Int, price: Double) {
        runOnUiThread(Runnable {

            SnapLog.print("discountApplied=========="+(discountApplied))
            totalAmount.text = CalculationUtils().getPriceText(this, quantityCount, price,discountApplied)

            if (price < restaurant.minOrder.toInt()) {
                discount.text =
                    TextUtils.concat(
                        "Add  ",
                        userInfo?.currency + (restaurant.minOrder.toInt() - price),
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