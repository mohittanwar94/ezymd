package com.ezymd.restaurantapp.itemdetail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import kotlinx.android.synthetic.main.bottom_sheet_location.*


class ItemDetailActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ItemDetailViewModel::class.java)
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


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

}