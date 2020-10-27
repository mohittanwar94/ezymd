package com.ezymd.restaurantapp.ui.home

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.ui.home.adapter.BannerPagerAdapter
import com.ezymd.restaurantapp.ui.home.trending.TrendingAdapter
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment : Fragment() {

    private var isNullViewRoot = false
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var airLocation: AirLocation
    private var locationModel = LocationModel()
    private var viewRoot: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        isNullViewRoot = false
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_home, container, false)
            isNullViewRoot = true
        }
        return viewRoot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SnapLog.print("onActivityCreated")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNullViewRoot) {
            setAdapterTrending()

            setBannerPager()
            setLocationListener()


            setListenerView()
        }
    }

    override fun onStart() {
        super.onStart()

    }

    private fun setLocationAddress(location: String, city: String) {
        var address = location
        if (address.startsWith("Unnamed Road,"))
            address = address.replace("Unnamed Road,", "").trim()

        delivery_location.text = city
        locationValue.text = /*if (address.length > 25) {
            address.substring(0, 22) + "...".trim()
        } else {*/
            address.trim()
        //  }
    }

    private fun setListenerView() {
        locationValue.setOnClickListener {
            location.performClick()
        }
        delivery_location.setOnClickListener {
            location.performClick()
        }
        location.setOnClickListener {
            UIUtil.clickAlpha(it)
            UIUtil.clickAlpha(locationValue)
            UIUtil.clickAlpha(delivery_location)
            (activity as MainActivity).startActivityFromFragment(
                this,
                Intent(activity, LocationActivity::class.java), JSONKeys.LOCATION_REQUEST
            )

        }
    }

    private fun setLocationListener() {
        airLocation = AirLocation(activity as MainActivity, object : AirLocation.Callback {
            override fun onSuccess(locations: ArrayList<Location>) {

                val location = locations[0]
                locationModel.lat = location.latitude
                locationModel.lang = location.longitude
                val geocoder = Geocoder(activity, Locale.getDefault())
                homeViewModel.getAddress(locationModel, geocoder)


            }

            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                ShowDialog(activity).disPlayDialog(locationFailedEnum.name, false, false)
            }
        }, true)
        airLocation.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JSONKeys.LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            locationModel = data!!.getParcelableExtra(JSONKeys.OBJECT) as LocationModel
            homeViewModel.address.postValue(locationModel)
        } else
            airLocation.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setAdapterRestaurant() {
        resturantRecyclerView.layoutManager = LinearLayoutManager(activity, VERTICAL, false)
        val restaurantAdapter =
            RestaurantAdapter(activity as MainActivity, OnRecyclerView { position, view ->

            })
        resturantRecyclerView.adapter = restaurantAdapter


    }

    private fun setBannerPager() {
        bannerPager.offscreenPageLimit = 1
        // bannerPager.setPageTransformer(true, AlphaPageTransformation())
        val registerationTutorialPagerAdapter =
            BannerPagerAdapter(
                activity as MainActivity,
                ArrayList<String>(),
                OnRecyclerView { position, view ->


                })
        bannerPager.adapter = registerationTutorialPagerAdapter
        dots_indicator.setViewPager(bannerPager)

        setPageChangeListener()

    }

    override fun onResume() {
        super.onResume()
        if (isNullViewRoot) {
            setAdapterRestaurant()
        }
        homeViewModel.isLoading.observe(this, androidx.lifecycle.Observer {

        })
        homeViewModel.address.observe(this, androidx.lifecycle.Observer {
            locationModel = it
            setLocationAddress(it.location, it.city)
        })


        (bannerPager.adapter as BannerPagerAdapter).startTimer(bannerPager, 5)
    }

    override fun onStop() {
        super.onStop()
        (bannerPager.adapter as BannerPagerAdapter).stopTimer()
    }

    private fun setPageChangeListener() {
        bannerPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setAdapterTrending() {
        trendingRecyclerView.setLayoutManager(LinearLayoutManager(activity, HORIZONTAL, false))
        val treandingAdapter =
            TrendingAdapter(activity as MainActivity, OnRecyclerView { position, view ->

            })
        trendingRecyclerView.adapter = treandingAdapter


    }
}