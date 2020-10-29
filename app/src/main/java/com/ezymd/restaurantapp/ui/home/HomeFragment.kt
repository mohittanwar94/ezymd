package com.ezymd.restaurantapp.ui.home

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.ui.home.adapter.BannerPagerAdapter
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.ui.home.model.Trending
import com.ezymd.restaurantapp.ui.home.trending.TrendingAdapter
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment : Fragment() {

    private var treandingAdapter: TrendingAdapter? = null
    private var restaurantAdapter: RestaurantAdapter? = null
    private var isNullViewRoot = false
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var airLocation: AirLocation
    private var locationModel = LocationModel()
    private var viewRoot: View? = null

    private val dataBanner = ArrayList<Resturant>()
    private val dataResturant = ArrayList<Resturant>()
    private val dataTrending = ArrayList<Trending>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo
    }

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
            askPermission()
            setListenerView()
            setAdapterRestaurant()

        }

    }

    private fun askPermission() {
        val isGranted = (activity as BaseActivity).checkLocationPermissions(object :
            BaseActivity.PermissionListener {
            override fun result(isGranted: Boolean) {
                if (isGranted) {
                    setLocationListener()
                    homeViewModel.getBanners(BaseRequest(userInfo))
                    homeViewModel.getResturants(BaseRequest(userInfo))
                    homeViewModel.getTrending(BaseRequest(userInfo))
                }

            }
        })
        if (isGranted)
            setLocationListener()

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
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                UIUtil.convertDpToPx(
                    activity,
                    requireActivity().resources.getDimension(R.dimen._3sdp)
                )
                    .toInt()
            )
        )
        restaurantAdapter =
            RestaurantAdapter(activity as MainActivity, OnRecyclerView { position, view ->
                val smallThumbnail = view.findViewById<RoundedImageView>(R.id.ivNotesThumb)
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                )
                (activity as MainActivity).startActivity(intent, optionsCompat.toBundle())
            }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    private fun setBannerPager() {
        bannerPager.offscreenPageLimit = 1
        bannerPager.clipToPadding = false;
        bannerPager.setPadding(20, 0, 40, 0);
        bannerPager.pageMargin = 20;

        // bannerPager.setPageTransformer(true, AlphaPageTransformation())
        val registerationTutorialPagerAdapter =
            BannerPagerAdapter(
                activity as MainActivity,
                dataBanner, OnRecyclerView { position, view ->
                    val smallThumbnail = view.findViewById<RoundedImageView>(R.id.imageView)
                    val intent = Intent(activity, DetailsActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                    )
                    (activity as MainActivity).startActivity(intent, optionsCompat.toBundle())

                })
        bannerPager.adapter = registerationTutorialPagerAdapter
        dots_indicator.setViewPager(bannerPager)

        setPageChangeListener()

    }

    override fun onResume() {
        super.onResume()
        if (isNullViewRoot) {
            if (dataBanner.size == 0) {
                homeViewModel.getBanners(BaseRequest(userInfo))
                homeViewModel.getResturants(BaseRequest(userInfo))
                homeViewModel.getTrending(BaseRequest(userInfo))
            }
        }
        setObservers()
        // (bannerPager.adapter as BannerPagerAdapter).startTimer(bannerPager, 5)
    }

    private fun setObservers() {
        homeViewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                content.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        })
        homeViewModel.address.observe(this, androidx.lifecycle.Observer {
            locationModel = it
            userInfo!!.lang = it.lang.toString()
            userInfo!!.lat = it.lat.toString()
            setLocationAddress(it.location, it.city)
        })


        homeViewModel.mPagerData.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataBanner.clear()
                dataBanner.addAll(it.data)
                bannerPager.adapter?.notifyDataSetChanged()

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mTrendingData.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataTrending.clear()
                treandingAdapter!!.setData(it.data)
                treandingAdapter!!.getData().let { it1 ->
                    dataTrending.addAll(it1)
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mResturantData.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                    resturantCount.text =
                        TextUtils.concat("" + dataResturant.size + " " + this.getString(R.string.resurant_around_you))
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })
        homeViewModel.errorRequest.observe(this, androidx.lifecycle.Observer {
            (activity as BaseActivity).showError(false, it, null)
        })


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
        treandingAdapter =
            TrendingAdapter(activity as MainActivity, OnRecyclerView { position, view ->

            }, dataTrending)
        trendingRecyclerView.adapter = treandingAdapter


    }
}