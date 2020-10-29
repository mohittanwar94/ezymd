package com.ezymd.restaurantapp.ui.home.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.SnapLog
import java.util.*


/**
 * Created by Mohit on 10/27/2016.
 */
class BannerPagerAdapter(
    private val mContext: Context,
    private val data: ArrayList<Resturant>,
    private val onRecyclerView: OnRecyclerView

) : PagerAdapter() {

    val handler: Handler = Handler(Looper.getMainLooper())
    var swipeTimer: Timer? = null
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(R.layout.banner_row, collection, false)
        val imageView = layout.findViewById<RoundedImageView>(R.id.imageView)
        GlideApp.with(mContext.applicationContext)
            .load(data[position].banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
        imageView.transitionName = "thumbnailTransition";
        layout.setOnClickListener { onRecyclerView.onClick(position, layout) }
        collection.addView(layout)
        SnapLog.print(data[position].banner)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun startTimer(myPager: ViewPager, time: Int) {
        val size: Int = data.size
        val Update: Runnable = object : Runnable {
            var NUM_PAGES = size
            var currentPage = myPager?.currentItem
            override fun run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0
                }
                myPager.setCurrentItem(currentPage++, true)
            }
        }
        swipeTimer = Timer()
        swipeTimer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 1000, (time * 1000).toLong())
    }

    fun stopTimer() {
        swipeTimer?.cancel()
        swipeTimer = null
    }
}