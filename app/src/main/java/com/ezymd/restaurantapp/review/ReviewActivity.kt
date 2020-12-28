package com.ezymd.restaurantapp.review

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_delivery_instruction.charterCount
import kotlinx.android.synthetic.main.activity_add_delivery_instruction.deliveryInstruction
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_review.progress
import kotlinx.android.synthetic.main.header_new.*
import kotlinx.android.synthetic.main.header_new.leftIcon

class ReviewActivity : BaseActivity() {

    private val item by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as OrderModel
    }


    private val viewModel by lazy {
        ViewModelProvider(this).get(ReviewViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        setToolBar()
        setHeaderData()
        setGUI()

        setObserver()
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
    }

    private fun setGUI() {
        leftIcon.setOnClickListener {
            onBackPressed()
        }
        deliveryRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            SnapLog.print("ratingBar=======" + ratingBar.rating)
            setRatingStatus(labelDelivery, ratingBar.rating)
        }

        restRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            SnapLog.print("ratingBar=======" + ratingBar.rating)
            setRatingStatus(label, ratingBar.rating)
        }

        submit.setOnClickListener {
            UIUtil.clickHandled(it)
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap["order_id"] = "" + item.orderId
            baseRequest.paramsMap["delivery_rating"] = "" + deliveryRating.rating
            baseRequest.paramsMap["restaurant_rating"] = "" + restRating.rating
            baseRequest.paramsMap["feedback"] = feedback.text.toString().trim()
            viewModel.saveRating(baseRequest)
        }
    }

    private fun setRatingStatus(labelDelivery: SnapTextView, rating: Float) {
        SnapLog.print("rating=======" + rating)
        if (rating <= 2f) {
            labelDelivery.text = getString(R.string.very_bad)
        } else if (rating > 2f && rating < 3f) {
            labelDelivery.text = getString(R.string.bad)
        } else if (rating == 3f && rating < 4f) {
            labelDelivery.text = getString(R.string.good)
        } else if (rating >= 4f) {
            labelDelivery.text = getString(R.string.very_good)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderData() {
        headertext.visibility = View.VISIBLE
        headertext.text = getString(R.string.orderID) + " #" + item.orderId

    }

    private fun setToolBar() {

        deliveryInstruction.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                charterCount.text = "" + deliveryInstruction.text.toString().length + "/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}