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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ezymd.restaurantapp.*
import com.ezymd.restaurantapp.dashboard.DashBoardActivity
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardNearByAdapter
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardPagerAdapter
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardTrendingAdapter
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.filters.FilterActivity
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.bannerPager
import kotlinx.android.synthetic.main.fragment_home.dots_indicator
import kotlinx.android.synthetic.main.fragment_home.emptyLay
import kotlinx.android.synthetic.main.fragment_home.emptymsg
import kotlinx.android.synthetic.main.fragment_home.enableLocation
import kotlinx.android.synthetic.main.fragment_home.filter
import kotlinx.android.synthetic.main.fragment_home.image
import kotlinx.android.synthetic.main.fragment_home.progress
import kotlinx.android.synthetic.main.fragment_home.resturantCount
import kotlinx.android.synthetic.main.fragment_home.resturantRecyclerView
import kotlinx.android.synthetic.main.fragment_home.trending
import kotlinx.android.synthetic.main.fragment_home.trendingRecyclerView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.collections.ArrayList


open class HomeFragment : Fragment() {
    private var locationChange = false
    private var treandingAdapter: DashBoardTrendingAdapter? = null
    private var restaurantAdapter: DashBoardNearByAdapter? = null
    private var registrationTutorialPagerAdapter: DashBoardPagerAdapter? = null
    private var isNullViewRoot = false
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var airLocation: AirLocation
    private var locationModel = LocationModel()
    private var viewRoot: View? = null

    private val dataBanner = ArrayList<DataTrending>()
    private val dataResturant = ArrayList<DataTrending>()
    private val dataTrending = ArrayList<DataTrending>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo!!
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

            lifecycleScope.launch(Dispatchers.IO) {
                homeViewModel.contentVisiblity(userInfo.configJson)
            }
            homeViewModel.getFilters(BaseRequest(userInfo))
            requireActivity().registerReceiver(
                mGpsSwitchStateReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )


            if (!userInfo.referalUrl.equals("")) {
                val baseRequest = BaseRequest(userInfo)
                baseRequest.paramsMap["referral_code"] = userInfo.referalUrl
                baseRequest.paramsMap["user_id"] = "" + userInfo.userID
                homeViewModel.saveReferral(baseRequest)
            }
        }

    }

    private fun setHeader(typeCategory: Int) {
        if (StoreType.Grocery == typeCategory) {
            trending.text = getString(R.string.trending_grocery)
        } else if (StoreType.Pharmacy == typeCategory) {
            trending.text = getString(R.string.trending_pharmacy)
        } else {
            trending.text = getString(R.string.trending_restaurnts)
        }
    }

    private fun setLocationEmpty() {
        emptyLay.visibility = View.VISIBLE
        emptymsg.text = getString(R.string.no_location_detected)
        image.setImageResource(R.drawable.ic_no_location)
        enableLocation.text = getString(R.string.enable_location)
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
        userInfo.address = address.trim()
        if (locationChange) {
            locationChange = false
            homeViewModel.getBanners(BaseRequest(userInfo))
            val baseRequest = BaseRequest(userInfo)
            homeViewModel.getResturants(baseRequest)
            homeViewModel.getTrending(baseRequest)
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
            locationModel = data!!.getParcelableExtra<LocationModel>(JSONKeys.LOCATION_OBJECT)!!
            homeViewModel.address.postValue(locationModel)
        } else if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_FIRST_USER) {
            dataResturant.clear()
            restaurantAdapter?.clearData()
            val baseRequest = BaseRequest(userInfo)
            homeViewModel.getResturants(baseRequest)
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
            DashBoardNearByAdapter(activity as MainActivity, OnRecyclerView { position, view ->
                val intent = Intent(requireActivity(), CategoryActivity::class.java)
                intent.putExtra(
                    JSONKeys.OBJECT,
                    dataResturant[position]
                )
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
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
        registrationTutorialPagerAdapter =
            DashBoardPagerAdapter(
                activity as MainActivity,
                dataBanner, OnRecyclerView { position, view ->
                    // val smallThumbnail = view.findViewById<RoundedImageView>(R.id.imageView)
                    val intent = Intent(activity, CategoryActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, dataBanner[position])
                    /*  val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                          (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                      )
                    */  (activity as MainActivity).startActivity(intent)//optionsCompat.toBundle())
                    EzymdApplication.getInstance().cartData.postValue(null)
                })
        bannerPager.adapter = registrationTutorialPagerAdapter
        dots_indicator.setViewPager(bannerPager)

        setPageChangeListener()

    }

    private fun getDataTrendingObject(resturant: Resturant): DataTrending {
        return DataTrending().apply {
            address = resturant.address
            lat = resturant.lat.toString()
            lang = resturant.longitude.toString()
            id = resturant.id
            name = resturant.name
            banner = resturant.banner
            category_id = -1
            cuisines = resturant.cuisines
            rating = resturant.rating
            min_order = resturant.minOrder
            discount = resturant.discount.toString()
            is_free_delivery = resturant.isFreeDelivery.toString()
            distance = resturant.distance
            phone_no = resturant.phoneNo
        }
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
        if (dataBanner.size == 0 && !userInfo.lat.equals("0.0")) {
            val baseRequest = BaseRequest(userInfo)
            homeViewModel.getBanners(baseRequest)
            homeViewModel.getResturants(baseRequest)
            homeViewModel.getTrending(baseRequest)
        }
        setObservers()

        // (bannerPager.adapter as BannerPagerAdapter).startTimer(bannerPager, 5)
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    private fun setObservers() {
        homeViewModel.isGroceryVisible.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                iv_grocery.visible()
                iv_grocery_text.visible()
            } else {
                iv_grocery.gone()
                iv_grocery_text.gone()
            }
        })

        homeViewModel.isAllHide.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it) {
                cardAvailOptions.gone()
            }
        })

        homeViewModel.isPharmacyVisible.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it) {
                iv_pharmacy.visible()
                iv_pharmacy_text.visible()
                iv_pharmacy_view.visible()

            } else {
                iv_pharmacy.gone()
                iv_pharmacy_text.gone()
                iv_pharmacy_view.gone()
            }
        })

        homeViewModel.primaryCategory.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            setHeader(it)
        })
        homeViewModel.isRestuantVisible.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it) {
                iv_food.visible()
                iv_food_text.visible()
                iv_food_view.visible()
            } else {
                iv_food.gone()
                iv_food_text.gone()
                iv_food_view.gone()
            }
        })
        homeViewModel.mReferralResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                userInfo.saveReferalUrl("")
            }
        })
        homeViewModel.isGPSEnable.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                setLocationEmpty()
            } else {
                if (delivery_location.text.equals("N/A")) {
                    SnapLog.print("n/a .............")
                    askPermission()
                }
                emptyLay.visibility = View.GONE
                if (dataBanner.size == 0 && !userInfo.lat.equals("0.0")) {
                    homeViewModel.getBanners(BaseRequest(userInfo))
                    val baseRequest = BaseRequest(userInfo)
                    homeViewModel.getResturants(baseRequest)
                    homeViewModel.getTrending(baseRequest)
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
            locationChange = true
            userInfo.lang = it.lang.toString()
            userInfo.lat = it.lat.toString()
            setLocationAddress(it.location, it.city)
        })


        homeViewModel.mPagerData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            emptyLay.visibility = View.GONE
            if (it.status == ErrorCodes.SUCCESS) {
                SnapLog.print("1=====")
                dataBanner.clear()
                registrationTutorialPagerAdapter?.notifyDataSetChanged()
                dataBanner.addAll(it.data)
                SnapLog.print("size=========" + dataBanner.size)
                registrationTutorialPagerAdapter?.notifyDataSetChanged()

                if (dataBanner.size > 0) {
                    homeViewModel.noBanner = false
                    bannerPager.visibility = View.VISIBLE
                } else {
                    homeViewModel.noBanner = true
                    bannerPager.visibility = View.GONE
                    checkEmpty()
                }
            } else {
                checkEmpty()
                dataBanner.clear()
                homeViewModel.noBanner = true
                bannerPager.visibility = View.GONE
                registrationTutorialPagerAdapter?.notifyDataSetChanged()
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mTrendingData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            emptyLay.visibility = View.GONE
            if (it.status == ErrorCodes.SUCCESS) {
                dataTrending.clear()
                treandingAdapter!!.setData(it.data)
                if (it.data.size > 0) {
                    trending.visibility = View.VISIBLE
                    homeViewModel.noTrending = false
                } else {
                    homeViewModel.noTrending = true
                    trending.visibility = View.GONE
                    checkEmpty()
                }
                treandingAdapter!!.getData().let { it1 ->
                    dataTrending.addAll(it1)
                }

            } else {
                checkEmpty()
                homeViewModel.noTrending = true
                dataTrending.clear()
                treandingAdapter!!.setData(dataTrending)
                trending.visibility = View.GONE
                dataTrending.clear()
                (activity as BaseActivity).showError(false, it.message, null)
            }
        })

        homeViewModel.mResturantData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            emptyLay.visibility = View.GONE
            if (it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                    if (it.data.size > 0) {
                        filter.visibility = View.VISIBLE
                        homeViewModel.noStores = false
                    } else {
                        homeViewModel.noStores = true
                        filter.visibility = View.GONE
                        checkEmpty()
                    }
                    resturantCount.text =
                        TextUtils.concat(
                            "" + dataResturant.size + " " + when (homeViewModel.primaryCategory.value) {
                                StoreType.RESTAURANT -> getString(R.string.resurant_around_you)
                                StoreType.Pharmacy -> getString(R.string.pharmacy_around_you)
                                else -> getString(R.string.grocery_around_you)
                            }
                        )
                }

            } else {
                homeViewModel.noStores = true
                checkEmpty()
                dataResturant.clear()
                restaurantAdapter?.setData(dataResturant)
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })
        homeViewModel.errorRequest.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null)
                (activity as BaseActivity).showError(false, it, null)
        })


    }

    private fun checkEmpty() {
        if (homeViewModel.noBanner && homeViewModel.noStores && homeViewModel.noTrending) {
            emptyLay.visibility = View.VISIBLE
            emptymsg.text = getString(R.string.no_data_found)
            image.setImageResource(R.drawable.ic_no_location)
            enableLocation.text = getString(R.string.change)
            enableLocation.setOnClickListener {
                location.performClick()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        homeViewModel.isLoading.removeObservers(viewLifecycleOwner)
        homeViewModel.mPagerData.removeObservers(viewLifecycleOwner)
        homeViewModel.mResturantData.removeObservers(viewLifecycleOwner)
        homeViewModel.mTrendingData.removeObservers(viewLifecycleOwner)
        homeViewModel.errorRequest.removeObservers(viewLifecycleOwner)
        homeViewModel.address.removeObservers(viewLifecycleOwner)
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
        trendingRecyclerView.layoutManager = LinearLayoutManager(activity, HORIZONTAL, false)
        trendingRecyclerView.addItemDecoration(
            HorizontialSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._10sdp
                ))
            )
        )
        treandingAdapter =
            DashBoardTrendingAdapter(activity as MainActivity, OnRecyclerView { position, view ->
                val intent = Intent(requireActivity(), CategoryActivity::class.java)
                intent.putExtra(
                    JSONKeys.OBJECT,
                    dataTrending[position]
                )
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
                EzymdApplication.getInstance().cartData.postValue(null)
            }, dataTrending)
        trendingRecyclerView.adapter = treandingAdapter


    }
}