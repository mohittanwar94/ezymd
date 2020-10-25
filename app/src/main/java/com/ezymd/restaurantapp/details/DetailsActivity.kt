package com.ezymd.restaurantapp.details

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ezymd.restaurantapp.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        //toolbar_layout.title = title
       // toolbar_layout.setCollapsedTitleTextColor(Color.BLACK)
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (mCurrentState != State.EXPANDED) {
                    toolbar_layout.title=""
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar_layout.title=""
                    toolbar.title = title
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    //onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE
            }

        })

    }
}