package com.ezymd.restaurantapp.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.AddressBottomSheet
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UIUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet_location.*
import java.util.*


class LocationActivity : BaseActivity(), OnMapReadyCallback {
    private var bottomSheetDialogFragment: AddressBottomSheet? = null
    private var mTimerIsRunning = false
    val REQUEST_CHECK_SETTINGS = 43


    private val CURRENT_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1321
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel by lazy {
        ViewModelProvider(this).get(LocationViewModel::class.java)
    }
    private var locationModel = LocationModel()
    private val gcd by lazy {
        Geocoder(this, Locale.getDefault())
    }

    private val handler by lazy {
        Handler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        setGUI()
        viewModel.address.observe(this, androidx.lifecycle.Observer {
            locationModel = it
            setLocationText(it.location)
        })

        viewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })

    }

    private fun setGUI() {
        if (intent.hasExtra(JSONKeys.LOCATION_OBJECT))
            done.text = getString(R.string.confirm_and_proceed)

        done.setOnClickListener {
            if (!intent.hasExtra(JSONKeys.LOCATION_OBJECT)) {
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(JSONKeys.LOCATION_OBJECT, locationModel)
                )
                finish()
            } else {
                //show address bottomsheet
                showAddressBottomSheet()
            }
        }
        change.setOnClickListener {
            UIUtil.clickAlpha(it)
            startSearchPlacesApi()
        }


    }

    private fun showAddressBottomSheet() {
        val tag = AddressBottomSheet.javaClass.name
        val oldFragment = supportFragmentManager.findFragmentByTag(tag)
        if (oldFragment != null) {
            supportFragmentManager.beginTransaction().remove(oldFragment).commit()
            bottomSheetDialogFragment = null
        }
        if (bottomSheetDialogFragment == null) {
            bottomSheetDialogFragment = AddressBottomSheet.newInstance(locationModel.location)
            bottomSheetDialogFragment!!.show(supportFragmentManager, tag)

        } else {
            if (!bottomSheetDialogFragment!!.isVisible) {
                bottomSheetDialogFragment!!.show(supportFragmentManager, tag)
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in,R.anim.right_out)
    }

    private fun startSearchPlacesApi() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.PHOTO_METADATAS
        )
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, CURRENT_PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return
        val success = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json
            )
        )

        if (!success) {
            SnapLog.print("Style parsing failed.")
        }

        val isGranted = checkLocationPermissions(object : PermissionListener {
            override fun result(isGranted: Boolean) {
                setReadyLocation(googleMap)
            }
        })
        if (isGranted) {
            setReadyLocation(googleMap)
        }

        myLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setReadyLocation(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.setAllGesturesEnabled(true)
        googleMap.setOnCameraMoveStartedListener(OnCameraMoveStartedListener {
            mTimerIsRunning = true
        })

        googleMap.setOnCameraIdleListener(OnCameraIdleListener { // Cleaning all the markers.
            googleMap.clear()
            if (mTimerIsRunning) {
                val center = googleMap.cameraPosition.target
                val zoom = googleMap.cameraPosition.zoom
                setStartLocation(center.latitude, center.longitude, "", zoom)
                mTimerIsRunning = false
            }

        })


        if (!intent.hasExtra(JSONKeys.LOCATION_OBJECT))
            getCurrentLocation()
        else {
            val location = intent.getParcelableExtra<LocationModel>(JSONKeys.LOCATION_OBJECT)
            setStartLocation(location.lat, location.lang, "", 17f)

        }

    }


    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result =
            LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result

                    val latLong = LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())
                    handler.postDelayed(runnnableAnim, 2000)
                    runnnableAnim.run()
                    Handler().postDelayed({
                        imgAnim1.visibility = View.GONE
                        imgAnim2.visibility = View.GONE
                    }, 3000)

                    val cameraPosition = CameraPosition.Builder()
                        .target(latLong).zoom(17f).build()

                    googleMap.animateCamera(
                        CameraUpdateFactory
                            .newCameraPosition(cameraPosition)
                    )

                    viewModel.getAddress(mLastLocation.latitude, mLastLocation.longitude, gcd)

                } else {
                    Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setLocationText(address: String) {
        var add = address
        if (add.startsWith("Unnamed Road,"))
            add = add.replace("Unnamed Road,", "")
        locationModel.location=add
        toLocationTxt.setText(add.trim())
    }


    private fun setStartLocation(lat: Double, lng: Double, addr: String, zoom: Float) {

        viewModel.getAddress(lat, lng, gcd)
        val latLong = LatLng(lat, lng)

        val cameraPosition = CameraPosition.Builder()
            .target(latLong).zoom(zoom).build()

        googleMap.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )

        handler.postDelayed(runnnableAnim, 2000)
        runnnableAnim.run()
        Handler().postDelayed({
            imgAnim1.visibility = View.GONE
            imgAnim2.visibility = View.GONE
        }, 3000)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (intent.hasExtra(JSONKeys.LOCATION_OBJECT)) {
                        val location =
                            intent.getParcelableExtra<LocationModel>(JSONKeys.LOCATION_OBJECT)
                        setStartLocation(location.lat, location.lang, "", 17f)
                    } else {
                        getCurrentLocation()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CURRENT_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AutocompleteActivity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val latLng = place.latLng.toString()
                    val location = latLng.substring(latLng.indexOf("(") + 1, latLng.indexOf(")"))
                    val loc = location.split(",")
                    setStartLocation(loc[0].toDouble(), loc[1].toDouble(), place.name!!, 17f)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    SnapLog.print(status.getStatusMessage())


                }
                AutocompleteActivity.RESULT_CANCELED -> {
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return true

    }


    val runnnableAnim = Runnable {
        imgAnim1.visibility = View.VISIBLE
        imgAnim2.visibility = View.VISIBLE
        kotlin.run {
            imgAnim1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(2000).withEndAction {
                kotlin.run {
                    imgAnim1.scaleX = 1f
                    imgAnim1.scaleY = 1f
                    imgAnim1.alpha = 1f
                }
            }

            imgAnim2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction {
                kotlin.run {
                    imgAnim2.scaleX = 1f
                    imgAnim2.scaleY = 1f
                    imgAnim2.alpha = 1f
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetDialogFragment?.dismissAllowingStateLoss()
        handler.removeCallbacks(runnnableAnim)
    }

    fun updateAddress(toString: String) {
        locationModel.location = toString
        bottomSheetDialogFragment?.dismissAllowingStateLoss()
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(JSONKeys.LOCATION_OBJECT, locationModel)
        )
        finish()

    }

}