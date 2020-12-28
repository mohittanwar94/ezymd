package com.ezymd.restaurantapp.coupon

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.coupon.adapter.CouponAdapter
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_coupon.*

class CouponActivity : BaseActivity() {

    private val dataResturant = ArrayList<ItemModel>()

    private val id by lazy {
        intent.getIntExtra(JSONKeys.ID, 0)
    }


    private val viewModel by lazy {
        ViewModelProvider(this).get(CouponViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)
        setToolBar()
        setHeaderData()
        setGUI()
        setObserver()
        getData()
    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("id", "" + id)
        viewModel.listCoupon(baseRequest)
    }

    private fun setObserver() {
        viewModel.showError().observe(this, Observer {
            showError(false, it, null)
        })

        viewModel.loginResponse.observe(this, Observer {

            if (it != null && it.status == ErrorCodes.SUCCESS) {
                showError(true, it.message, object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        onBackPressed()
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
                    }

                })
            } else {
                showError(false, it.message, null)
            }
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    private fun setGUI() {
        setAdapter()
    }


    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderData() {
        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.title_promo_code)

    }

    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }


    private fun setAdapter() {
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._5sdp
                ))
            )
        )
        val restaurantAdapter = CouponAdapter(this, OnRecyclerView { position, view ->

        }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }
}