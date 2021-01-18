package com.ezymd.restaurantapp.coupon

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.coupon.adapter.CouponAdapter
import com.ezymd.restaurantapp.coupon.model.CoupanModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_coupon.*

class CouponActivity : BaseActivity() {

    private val dataResturant = ArrayList<CoupanModel>()
    private var pos = 0
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
        baseRequest.paramsMap["id"] = "" + id
        viewModel.listCoupon(baseRequest)
    }

    private fun setObserver() {
        viewModel.showError().observe(this, Observer {
            showError(false, it, null)
        })

        viewModel.applyCoupon.observe(this, Observer {

            if (it != null && it.status == ErrorCodes.SUCCESS) {
                val intent = Intent()
                intent.putExtra(JSONKeys.OBJECT, dataResturant[pos])
                setResult(Activity.RESULT_OK, intent)
                showError(true, it.message, object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        finish()
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
                    }

                })
            } else {
                showError(false, it.message, null)
            }
        })
        viewModel.loginResponse.observe(this, Observer {

            if (it != null && it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                dataResturant.addAll(it.data)
                setAdapter()
                showEmpty(dataResturant.size)
            } else {
                showError(false, it.message, null)
            }
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    private fun showEmpty(size: Int) {
        emptyView.visibility = if (size == 0) View.VISIBLE else View.GONE
    }

    private fun setGUI() {
        //setAdapter()
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
            DividerItemDecoration(this, RecyclerView.VERTICAL)
        )
        val restaurantAdapter = CouponAdapter(this, OnRecyclerView { position, view ->
            applyCoupon(view, dataResturant[position])
            pos = position
        }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }


    fun applyCoupon(it: View, coupanModel: CoupanModel) {
        UIUtil.clickAlpha(it)
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["coupon_id"] = "" + coupanModel.id
        baseRequest.paramsMap["user_id"] = "" + userInfo!!.userID
        viewModel.applyCoupon(baseRequest)

    }

    fun showBottomSheet(it: View, item: CoupanModel) {
        UIUtil.clickAlpha(it)
        val sheetDialog = BottomSheetDialog(this)
        sheetDialog.setContentView(R.layout.tnc_bottom_sheet)
        val promoName = sheetDialog.findViewById<TextView>(R.id.name)
        promoName!!.text = item.couponCode
        val description = sheetDialog.findViewById<TextView>(R.id.description)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            description!!.text = Html.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            description!!.text = Html.fromHtml(item.description)
        }
        sheetDialog.setCanceledOnTouchOutside(true)
        sheetDialog.show()
    }

}