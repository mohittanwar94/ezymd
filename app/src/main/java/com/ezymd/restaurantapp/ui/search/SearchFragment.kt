package com.ezymd.restaurantapp.ui.search

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.*
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardNearByAdapter
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var restaurantAdapter: DashBoardNearByAdapter? = null
    private var isNullViewRoot = false
    private lateinit var searchViewModel: SearchViewModel
    private var viewRoot: View? = null

    private val dataResturant = ArrayList<DataTrending>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        isNullViewRoot = false
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_search, container, false)
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
            setAdapterRestaurant()
            askPermission()
            GlobalScope.launch {
                searchViewModel.contentVisiblity(userInfo!!.configJson)
            }
            setSearchListerner()
            requireActivity().registerReceiver(
                mGpsSwitchStateReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }

    }

    private fun askPermission() {
        val isGranted = (activity as BaseActivity).checkLocationPermissions(object :
            BaseActivity.PermissionListener {
            override fun result(isGranted: Boolean) {
                if (!isGranted) {
                    setLocationEmpty()
                }

            }
        })
        if (!isGranted)
            setLocationEmpty()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(
                mGpsSwitchStateReceiver
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSearchListerner() {
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) && search.text.toString()
                        .trim().isNotEmpty()
                ) {
                    (activity as BaseActivity).showError(
                        false,
                        requireActivity().getString(R.string.please_enable_location),
                        null
                    )
                    return
                }
                if (search.text.toString().trim().length > 3) {
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("search", search.text.toString())
                    searchViewModel.searchRestaurants(baseRequest)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (search.text.toString().trim().isEmpty()) {
                    val baseRequest = BaseRequest(userInfo)
                    searchViewModel.searchRestaurants(baseRequest)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }


    private fun setAdapterRestaurant() {
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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
                val smallThumbnail = view.findViewById<RoundedImageView>(R.id.ivNotesThumb)
                val intent = Intent(activity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                )
                (activity as MainActivity).startActivity(intent, optionsCompat.toBundle())
                EzymdApplication.getInstance().cartData.postValue(null)
            }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    override fun onResume() {
        super.onResume()
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        // SnapLog.print( "start status=============" + !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER))
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
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

        emptyLay.visibility = View.GONE
        setObservers()
    }

    private val mGpsSwitchStateReceiver: BroadcastReceiver = object : GpsLocationReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?

                searchViewModel.isGPSEnable.postValue(
                    locationManager!!.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    )
                )

            }
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
                    || ContextCompat.checkSelfPermission(
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

    private fun setObservers() {
        searchViewModel.isGPSEnable.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                setLocationEmpty()
            } else {

                emptyLay.visibility = View.GONE

            }
        })
        searchViewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (!it) {
                progress.visibility = View.GONE
            } else {
                progress.visibility = View.VISIBLE
            }
        })

        searchViewModel.primaryCategory.observe(viewLifecycleOwner, Observer {
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap["category_id"] = "" + it
            searchViewModel.getResturants(baseRequest)
            when (it) {
                StoreType.RESTAURANT -> search.setHint(getString(R.string.search_resturant))
                StoreType.Pharmacy -> search.setHint(getString(R.string.search_pharmacy))
                StoreType.Grocery -> search.setHint(getString(R.string.search_grocery_stores))
            }

        })

        searchViewModel.mSearchData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })

        searchViewModel.mResturantData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                dataResturant.clear()
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })
        searchViewModel.errorRequest.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            (activity as BaseActivity).showError(false, it, null)
        })


    }

    override fun onStop() {
        super.onStop()
        searchViewModel.isLoading.removeObservers(viewLifecycleOwner)
        searchViewModel.mResturantData.removeObservers(viewLifecycleOwner)
        searchViewModel.errorRequest.removeObservers(viewLifecycleOwner)
        searchViewModel.mSearchData.removeObservers(viewLifecycleOwner)
    }


}