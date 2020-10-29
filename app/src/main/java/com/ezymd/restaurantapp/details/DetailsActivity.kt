package com.ezymd.restaurantapp.details

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.UIUtil
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_scrolling.*

class DetailsActivity : AppCompatActivity() {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setToolBar()
        setHeaderData()
    }

    private fun setHeaderData() {
        val resturant = intent.getSerializableExtra(JSONKeys.OBJECT) as Resturant
        GlideApp.with(applicationContext)
            .load(resturant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(image)

        toolbar_layout.title = resturant.name
        name.text = resturant.name
        category.text = resturant.category
        distance.text = TextUtils.concat("" + UIUtil.round(resturant.distance, 1), " km")
        rating.text = if (resturant.rating > 0) "" + resturant.rating else "N/A"
        minimumOrder.text = if (resturant.minOrder.equals("0")) "N/A" else resturant.minOrder + getString(
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
        toolbar_layout.title = ""

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (mCurrentState != State.EXPANDED) {
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar.setTitleTextColor(Color.BLACK)
                    toolbar.setBackgroundColor(Color.WHITE)
                    window.statusBarColor = Color.WHITE;
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