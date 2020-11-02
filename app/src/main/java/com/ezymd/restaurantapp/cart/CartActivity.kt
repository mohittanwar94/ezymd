package com.ezymd.restaurantapp.cart

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
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.VerticalSpacesItemDecoration
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.content_scrolling.*

class CartActivity : BaseActivity() {
    val serviceAmount = 10
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
        setObserver()
    }

    private fun setObserver() {
        EzymdApplication.getInstance().cartData.observe(this, Observer {
            if (it != null) {
                if (it.size > 0)
                    notifyAdapter(it)
            }
        })

        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
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
        viewModel.mResturantData.removeObservers(this)
        viewModel.errorRequest.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        EzymdApplication.getInstance().cartData.removeObservers(this)
    }


    private fun notifyAdapter(it: ArrayList<ItemModel>) {
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
                "" + serviceAmount
            )
            totalAmount.text = TextUtils.concat(
                getString(R.string.dollor),
                "" + (serviceAmount + price)
            )
            subTotal.text = TextUtils.concat(
                getString(R.string.dollor),
                "" + price
            )

            if (price < restaurant.minOrder.toInt()) {
                discount.text =
                    TextUtils.concat(
                        "Add ",
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