package com.ezymd.restaurantapp.reviewsList

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.reviewsList.adapter.RatingsAdapter
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorCodes
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_reviews_list.*
import java.util.*

class ReviewsListActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(ReviewsListViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews_list)
        setGUI()
        setObserver()
        fetchData()
    }

    private fun setObserver() {
        viewModel.showError().observe(this, {
            showError(false, it, null)
        })

        viewModel.loginResponse.observe(this, Observer {

            if (it != null && it.status == ErrorCodes.SUCCESS) {
                showError(true, it.message, object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        setResult(Activity.RESULT_OK)
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
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })
        viewModel.excellentRating.observe(this, {
            rate_excellent.progress = it
        })
        viewModel.goodRating.observe(this, {
            rate_good.progress = it
        })
        viewModel.averageRating.observe(this, {
            rate_average.progress = it
        })
        viewModel.belowAverage.observe(this, {
            rate_average.progress = it
        })
        viewModel.poor.observe(this, {
            rate_poor.progress = it
        })
        viewModel.total.observe(this, {
            tv_rating.text = String.format(Locale.getDefault(), "%.01f", it.toDouble())
        })
        viewModel.total.observe(this, {
            total_reviews.text = "based on $it reviews"
        })
        viewModel.listing.observe(this, {
            val restaurantAdapter = RatingsAdapter(this, it)
            rv_reviews?.adapter = restaurantAdapter
        })
    }

    private fun setGUI() {
        leftIcon.setOnClickListener {
            onBackPressed()
        }


    }

    private fun fetchData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["shop_id"] = "11"
        viewModel.getShopReview(baseRequest)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}