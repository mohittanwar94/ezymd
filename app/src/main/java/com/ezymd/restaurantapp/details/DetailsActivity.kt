package com.ezymd.restaurantapp.details

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.UIUtil
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_scrolling.*

class DetailsActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }
    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Resturant
    }

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        window.sharedElementEnterTransition =
            TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation);
        image.transitionName = "thumbnailTransition";

        getData()
        setToolBar()
        setHeaderData()
    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("id", "" + restaurant.id)
        viewModel.getDetails(baseRequest)
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {

        })


        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun setHeaderData() {

        GlideApp.with(applicationContext)
            .load(restaurant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(image)

        toolbar_layout.title = restaurant.name
        name.text = restaurant.name
        category.text = restaurant.category
        distance.text = TextUtils.concat("" + UIUtil.round(restaurant.distance, 1), " km")
        rating.text = if (restaurant.rating > 0) "" + restaurant.rating else "N/A"
        minimumOrder.text =
            if (restaurant.minOrder.equals("0")) "N/A" else restaurant.minOrder + getString(
                R.string.dollor
            )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
        window.statusBarColor = Color.TRANSPARENT

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (Math.abs(verticalOffset) != verticalOffset) {
                    toolbar_layout.setCollapsedTitleTextColor(Color.TRANSPARENT)
                }

                if (mCurrentState != State.EXPANDED) {
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar.setTitleTextColor(Color.BLACK)
                    toolbar.setBackgroundColor(Color.WHITE)
                    window.statusBarColor = Color.WHITE
                    toolbar_layout.setCollapsedTitleTextColor(Color.BLACK)
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    toolbar.setTitleTextColor(Color.TRANSPARENT)
                    window.statusBarColor = Color.TRANSPARENT
                }
                mCurrentState = State.IDLE
            }

        })

    }
}