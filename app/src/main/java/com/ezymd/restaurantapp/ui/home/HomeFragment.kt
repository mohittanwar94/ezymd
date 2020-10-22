package com.ezymd.restaurantapp.ui.home

import android.content.Intent
import android.location.Address
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
import com.ezymd.restaurantapp.ui.home.adapter.BannerPagerAdapter
import com.ezymd.restaurantapp.ui.home.trending.TrendingAdapter
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.ShowDialog
import kotlinx.android.synthetic.main.fragment_home.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment : Fragment() {


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var airLocation: AirLocation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapterTrending()
        setAdapterRestaurant()
        setBannerPager()
        setLocationListener()
        airLocation.start() 
    }

    private fun setLocationListener() {
        airLocation = AirLocation(activity as MainActivity, object : AirLocation.Callback {
            override fun onSuccess(locations: ArrayList<Location>) {
                val location = locations[0]
                val addresses: List<Address>
                val geocoder: Geocoder = Geocoder(activity, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                val builder = StringBuilder()
                if (addresses[0].getAddressLine(0) != null) {
                    builder.append(addresses[0].getAddressLine(0))
                }
                if (addresses[0].getLocality() != null) {
                    builder.append(addresses[0].getLocality())
                }
                if (addresses[0].getAdminArea() != null) {
                    builder.append(addresses[0].getAdminArea())
                }
                if (addresses[0].getCountryName() != null) {
                    builder.append(addresses[0].getCountryName())
                }
                if (addresses[0].getPostalCode() != null) {
                    builder.append(addresses[0].getPostalCode())
                }
                if (addresses[0].getFeatureName() != null) {
                    builder.append(addresses[0].getFeatureName())
                }

                locationValue.text = if (builder.toString().length > 25) {
                    builder.toString().substring(0, 22) + "..."
                } else {
                    builder.toString()
                }
            }

            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                ShowDialog(activity).disPlayDialog(locationFailedEnum.name, false, false)
            }
        }, true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        bannerPager.offscreenPageLimit = 4
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
        (bannerPager.adapter as BannerPagerAdapter).startTimer(bannerPager,5)
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