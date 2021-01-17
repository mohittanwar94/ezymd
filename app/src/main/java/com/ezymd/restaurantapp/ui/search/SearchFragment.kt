package com.ezymd.restaurantapp.ui.search

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.GpsLocationReceiver
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.ui.home.RestaurantAdapter
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private var restaurantAdapter: RestaurantAdapter? = null
    private var isNullViewRoot = false
    private lateinit var searchViewModel: SearchViewModel
    private var viewRoot: View? = null

    private val dataResturant = ArrayList<Resturant>()

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
            searchViewModel.getResturants(BaseRequest(userInfo))
            setSearchListerner()
            requireActivity().registerReceiver(
                mGpsSwitchStateReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(
            mGpsSwitchStateReceiver
        )
    }

    private fun setSearchListerner() {
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) && search.text.toString()
                        .trim().length > 0
                ) {
                    (activity as BaseActivity).showError(
                        false,
                        requireActivity().getString(R.string.please_enable_location),
                        null
                    )
                    return
                }
                if (search.text.toString().trim().length > 4) {
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

    override fun onResume() {
        super.onResume()
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setLocationEmpty()
            return
        } else {
            emptyLay.visibility = View.GONE

        }
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
        emptymsg.text = getString(R.string.enable_location_to_use_app)
        image.setImageResource(R.drawable.ic_location)
        enableLocation.setOnClickListener {
            UIUtil.clickHandled(it)
            val callGPSSettingIntent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(callGPSSettingIntent)
        }
    }

    private fun setObservers() {
        searchViewModel.isGPSEnable.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                setLocationEmpty()
            } else {

                emptyLay.visibility = View.GONE

            }
        })
        searchViewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                progress.visibility = View.GONE
            } else {
                progress.visibility = View.VISIBLE
            }
        })

        searchViewModel.mSearchData.observe(this, androidx.lifecycle.Observer {
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

        searchViewModel.mResturantData.observe(this, androidx.lifecycle.Observer {
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
        searchViewModel.errorRequest.observe(this, androidx.lifecycle.Observer {
            (activity as BaseActivity).showError(false, it, null)
        })


    }

    override fun onStop() {
        super.onStop()
        searchViewModel.isLoading.removeObservers(this)
        searchViewModel.mResturantData.removeObservers(this)
        searchViewModel.errorRequest.removeObservers(this)
        searchViewModel.mSearchData.removeObservers(this)
    }


}