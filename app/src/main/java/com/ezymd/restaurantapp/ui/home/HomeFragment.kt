package com.ezymd.restaurantapp.ui.home

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ezymd.restaurantapp.*
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.dashboard.DashBoardActivity
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.filters.FilterActivity
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.ui.home.adapter.BannerPagerAdapter
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.ui.home.model.Trending
import com.ezymd.restaurantapp.ui.home.trending.TrendingAdapter
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment : Fragment() {
    private var locationChange = false
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
            homeViewModel.getFilters(BaseRequest(userInfo))
            requireActivity().registerReceiver(
                mGpsSwitchStateReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )


        }

    }

    private fun setLocationEmpty() {
        emptyLay.visibility = View.VISIBLE
        emptymsg.text = getString(R.string.no_location_detected)
        image.setImageResource(R.drawable.ic_no_location)
        enableLocation.setOnClickListener {
            UIUtil.clickHandled(it)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    val myAppSettings = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${requireActivity().packageName}")
                    )
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    if (myAppSettings.resolveActivity(requireActivity().packageManager) != null)
                        startActivity(myAppSettings)
                    return@setOnClickListener
                }
            }
            val callGPSSettingIntent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(callGPSSettingIntent)

        }
    }

    private val mGpsSwitchStateReceiver: BroadcastReceiver = object : GpsLocationReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                val locationManager =
                    requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?

                homeViewModel.isGPSEnable.postValue(
                    locationManager!!.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    )
                )

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(mGpsSwitchStateReceiver)

        } catch (e: Exception) {
        }
    }

    private fun askPermission() {
        val isGranted = (activity as BaseActivity).checkLocationPermissions(object :
            BaseActivity.PermissionListener {
            override fun result(isGranted: Boolean) {
                if (isGranted) {
                    setLocationListener()

                } else {
                    setLocationEmpty()
                }

            }
        })
        if (isGranted)
            setLocationListener()
        else {
            setLocationEmpty()
        }

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
        userInfo!!.address = address.trim()
        if (dataBanner.size == 0 || locationChange) {
            locationChange = false
            homeViewModel.getBanners(BaseRequest(userInfo))
            homeViewModel.getResturants(BaseRequest(userInfo))
            homeViewModel.getTrending(BaseRequest(userInfo))
        }
        //  }
    }

    private fun setListenerView() {
        iv_food.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityDashBoard(StoreType.RESTAURANT)
        }

        iv_grocery.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityDashBoard(StoreType.Grocery)
        }
        iv_pharmacy.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityDashBoard(StoreType.Pharmacy)
        }
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

    private fun startActivityDashBoard(grocery: Int) {
        val valueIntent = Intent(requireActivity(), DashBoardActivity::class.java)
        valueIntent.putExtra(JSONKeys.TYPE, grocery)
        requireActivity().startActivity(valueIntent)
        activity?.overridePendingTransition(R.anim.left_in, R.anim.left_out)
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
            locationChange = true
            locationModel = data!!.getParcelableExtra(JSONKeys.LOCATION_OBJECT) as LocationModel
            homeViewModel.address.postValue(locationModel)
        } else if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_FIRST_USER) {
            dataResturant.clear()
            restaurantAdapter?.clearData()
            homeViewModel.getResturants(BaseRequest(userInfo))
            //clearAllFilter()
        } else if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_OK) {
            //applyAllFilter()
            dataResturant.clear()
            restaurantAdapter?.clearData()
            restaurantAdapter?.notifyDataSetChanged()
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap.putAll(data?.getSerializableExtra(JSONKeys.FILTER_MAP) as HashMap<String, String>)
            homeViewModel.getResturants(baseRequest)

        } else if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_CANCELED) {
            //applyAllFilter()
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
        filter.setOnClickListener {
            requireActivity().startActivityFromFragment(
                this, Intent(requireActivity(), FilterActivity::class.java),
                JSONKeys.FILTER
            )
            requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)

        }
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
                EzymdApplication.getInstance().cartData.postValue(null)
            }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    private fun setBannerPager() {
        bannerPager.offscreenPageLimit = 1
        bannerPager.clipToPadding = false;
        bannerPager.setPadding(20, 0, 40, 0);
        bannerPager.pageMargin = 20;

        // bannerPager.setPageTransformer(true, AlphaPageTransformation())
        val registrationTutorialPagerAdapter =
            BannerPagerAdapter(
                activity as MainActivity,
                dataBanner, OnRecyclerView { position, view ->
                    val smallThumbnail = view.findViewById<RoundedImageView>(R.id.imageView)
                    val intent = Intent(activity, DetailsActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, dataBanner[position])
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                    )
                    (activity as MainActivity).startActivity(intent, optionsCompat.toBundle())
                    EzymdApplication.getInstance().cartData.postValue(null)
                })
        bannerPager.adapter = registrationTutorialPagerAdapter
        dots_indicator.setViewPager(bannerPager)

        setPageChangeListener()

    }

    override fun onResume() {
        super.onResume()
        val locationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?
        // SnapLog.print( "start status=============" + !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER))
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                setLocationEmpty()
                return
            }
        }
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocationEmpty()
            return
        }

        if (delivery_location.text.equals("N/A")) {
            SnapLog.print("n/a .............")
            askPermission()
        }
        emptyLay.visibility = View.GONE
        if (dataBanner.size == 0 && !userInfo!!.lat.equals("0.0")) {
            homeViewModel.getBanners(BaseRequest(userInfo))
            homeViewModel.getResturants(BaseRequest(userInfo))
            homeViewModel.getTrending(BaseRequest(userInfo))
        }
        setObservers()

        // (bannerPager.adapter as BannerPagerAdapter).startTimer(bannerPager, 5)
    }

    private fun setObservers() {
        homeViewModel.isGPSEnable.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                setLocationEmpty()
            } else {
                if (delivery_location.text.equals("N/A")) {
                    SnapLog.print("n/a .............")
                    askPermission()
                }
                emptyLay.visibility = View.GONE
                if (dataBanner.size == 0 && !userInfo!!.lat.equals("0.0")) {
                    homeViewModel.getBanners(BaseRequest(userInfo))
                    homeViewModel.getResturants(BaseRequest(userInfo))
                    homeViewModel.getTrending(BaseRequest(userInfo))
                }

            }
        })
        homeViewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                content.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        })
        homeViewModel.address.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            locationModel = it
            userInfo!!.lang = it.lang.toString()
            userInfo!!.lat = it.lat.toString()
            setLocationAddress(it.location, it.city)
        })


        homeViewModel.mPagerData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataBanner.clear()
                bannerPager.adapter?.notifyDataSetChanged()
                dataBanner.addAll(it.data)
                SnapLog.print("size=========" + dataBanner.size)
                bannerPager.adapter?.notifyDataSetChanged()

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mTrendingData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataTrending.clear()
                treandingAdapter!!.setData(it.data)
                if (it.data.size > 0)
                    trending.text = getString(R.string.trending_food)
                treandingAdapter!!.getData().let { it1 ->
                    dataTrending.addAll(it1)
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mResturantData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                    if (it.data.size > 0)
                        filter.visibility = View.VISIBLE
                    else
                        filter.visibility = View.GONE
                    resturantCount.text =
                        TextUtils.concat("" + dataResturant.size + " " + this.getString(R.string.resurant_around_you))
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })
        homeViewModel.errorRequest.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null)
                (activity as BaseActivity).showError(false, it, null)
        })


    }


    override fun onStop() {
        super.onStop()
        homeViewModel.isLoading.removeObservers(viewLifecycleOwner)
        homeViewModel.mPagerData.removeObservers(viewLifecycleOwner)
        homeViewModel.mResturantData.removeObservers(viewLifecycleOwner)
        homeViewModel.mTrendingData.removeObservers(viewLifecycleOwner)
        homeViewModel.errorRequest.removeObservers(viewLifecycleOwner)
        homeViewModel.address.removeObservers(viewLifecycleOwner)
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
                val obj = dataTrending[position]
                if (obj.restaurant != null) {
                    val intent = Intent(activity, DetailsActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, obj.restaurant)
                    (activity as MainActivity).startActivity(intent)
                    EzymdApplication.getInstance().cartData.postValue(null)
                }
            }, dataTrending)
        trendingRecyclerView.adapter = treandingAdapter


    }
}