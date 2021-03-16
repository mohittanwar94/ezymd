package com.ezymd.restaurantapp.itemdetail

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.JSONKeys
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.bottom_sheet_location.*
import kotlinx.android.synthetic.main.cart_item_row.view.*


class ItemDetailActivity : BaseActivity() {

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

        viewModel.isLoading.observe(this, {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })

    }

    private fun setGUI() {

        tv_name?.text = product.item
        tv_desc?.text = product.description
        tv_desc?.price?.text = "${getString(R.string.dollor)}${product.price}"
        if (!TextUtils.isEmpty(product.image)) {
            GlideApp.with(applicationContext)
                .load(product.image).centerCrop().override(200, 200).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_icon)
        } else {
            iv_icon?.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

}