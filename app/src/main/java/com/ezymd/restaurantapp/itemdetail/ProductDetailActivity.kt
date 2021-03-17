package com.ezymd.restaurantapp.productdetail

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.CartActivity
import com.ezymd.restaurantapp.customviews.ValueChangedListener
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.itemdetail.ItemDetailViewModel
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.cart_view.*
import kotlinx.android.synthetic.main.content_scrolling_category.*


class ProductDetailActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ItemDetailViewModel::class.java)
    }
    private val product by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Product
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setGUI()


        setObserver()
    }

    private fun setObserver() {
        EzymdApplication.getInstance().cartData.observe(this, Observer {
            if (it != null) {
                checkDataChanges(it)
                processCartData(it)
            }
        })


        viewModel.errorRequest.observe(this, {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })

       /* image.setOnClickListener {
            UIUtil.clickAlpha(it)
            if (bannerList.size > 0)
                ShowImageVideo(this).Display(bannerList, 0)
        }

        viewCart.setOnClickListener {
            val intent = Intent(this@ProductDetailActivity, CartActivity::class.java)
            intent.putExtra(JSONKeys.OBJECT, getRestaurantObject(restaurant))
            startActivity(intent)
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
*/

    }


    private fun checkDataChanges(it: ArrayList<ItemModel>) {
     /*   var i = 0
        for (item in dataResturant) {
            SnapLog.print("data quantity===" + item.quantity)
            if (it.size == 0)
                item.quantity = 0
            dataResturant[i] = item
            for (itemModel in it) {
                if (itemModel.id == item.id) {
                    SnapLog.print("quantity===" + itemModel.quantity)
                    dataResturant[i] = itemModel
                    break
                }
            }
            i++
        }*/

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

    var isExanded = false
    private fun setCartData(quantity: Int, price: Int) {

        if (quantity == 0 && price == 0) {
            //cartView.visibility= View.GONE
            slideDown(cartView)
            isExanded = false
        } else {
            // cartView.visibility= View.VISIBLE
            if (!isExanded)
                slideUp(cartView)
            isExanded = true
            setCartDetails(quantity, price)
        }


    }

    private fun setCartDetails(quantityCount: Int, price: Int) {
        runOnUiThread(Runnable {
            quantity.text = TextUtils.concat(
                "" + quantityCount,
                " ",
                getString(R.string.items),
                " | ",
                getString(R.string.dollor),
                "" + price
            )
        })

    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,                 // fromXDelta
            0f,                 // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        )             // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    private fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,                 // fromXDelta
            0f,                 // toXDelta
            0f,                 // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
            }
        })
        view.startAnimation(animate)
    }

    private fun setGUI() {

        tv_name?.text = product.item
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv_desc?.text = Html.fromHtml(product.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            tv_desc?.text = Html.fromHtml(product.description)
        }
        tv_price?.text = "${getString(R.string.dollor)}${product.price}"
        if (!TextUtils.isEmpty(product.image)) {
            GlideApp.with(applicationContext)
                .load(product.image).centerCrop().override(200, 200).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_icon)
        } else {
            iv_icon?.visibility = View.GONE
        }

        leftIcon.setOnClickListener {
            UIUtil.clickAlpha(it)
            onBackPressed()
        }

        if (product.qnty != 0) {
            add.visibility = View.GONE
            quantityPicker.visibility = View.VISIBLE
            quantityPicker.increment(product.qnty)
        }

        add.setOnClickListener {
            if (product.stock == 0) {
                viewModel.errorRequest.value =
                    getString(R.string.this_item_is_not_available)
                return@setOnClickListener
            }
            it.animate().alpha(0f).setDuration(250).start()
            quantityPicker.alpha = 0.0f
            quantityPicker.visibility = View.VISIBLE
            quantityPicker.animate().alpha(1f).setDuration(250).start()

            quantityPicker.postDelayed(Runnable {
                add.visibility = View.GONE
                quantityPicker.increment(1)
                product.qnty = 1
                quantityPicker.alpha = 1f
                viewModel.addToCart(getItemModelObject(product))

            }, 50)
        }


        quantityPicker.setLimitExceededListener { limit, exceededValue ->

        }


        quantityPicker.valueChangedListener =
            ValueChangedListener { value, action ->
                SnapLog.print("quantity====" + value)
                if (value < 1) {
                    quantityPicker.animate().alpha(0f).setDuration(250).start()
                    product.qnty = 0
                    add.alpha = 0.0f
                    add.visibility = View.VISIBLE
                    add.animate().alpha(1f).setDuration(250)
                        .setUpdateListener { animation ->
                            add.alpha =
                                (250 / if (animation!!.currentPlayTime <= 0) 1 else animation.currentPlayTime).toFloat()
                        }.start()
                    quantityPicker.value = 0
                    viewModel.removeItem(getItemModelObject(product))
                } else {

                    product.qnty = value
                    viewModel.addToCart(getItemModelObject(product))
                }

            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

}