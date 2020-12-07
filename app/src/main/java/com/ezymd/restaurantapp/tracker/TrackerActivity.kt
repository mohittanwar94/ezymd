package com.ezymd.restaurantapp.tracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot


class TrackerActivity : BaseActivity(), OnMapReadyCallback {


    private val mMarkers: HashMap<String, Marker> = HashMap()
    private var mMap: GoogleMap? = null
    private val trackViewModel by lazy { ViewModelProvider(this).get(TrackerViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracker_activity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


    }

    private fun setObserver() {
        var lat = 28.971880
        var lng = 77.673700
        val source = LatLng(lat, lng)
        lat = 28.15179145
        lng = 77.34360700000001
        val destination = LatLng(lat, lng)

        val hashMap = trackViewModel.getDirectionsUrl(
            source,
            destination,
            getString(R.string.google_maps_key)
        )
        trackViewModel.downloadRoute(hashMap)
        trackViewModel.routeInfoResponse.observe(this, Observer {
            if (it != null)
                generateRouteOnMap(it)
        })

        trackViewModel.showError().observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        trackViewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })


        trackViewModel.firebaseResponse.observe(this, Observer {
            if (it != null)
                setMarker(it)
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap!!.setMaxZoomPreference(16f)
        trackViewModel.loginToFirebase(
            getString(R.string.firebase_email),
            getString(R.string.firebase_password), getString(R.string.firebase_path)
        )
        setObserver()
    }


    private fun setMarker(dataSnapshot: DataSnapshot) {

        val key = dataSnapshot.key
        val value = dataSnapshot.value as HashMap<*, *>
        val lat = (value["latitude"].toString()).toDouble()
        val lng = (value["longitude"].toString()).toDouble()
        val location = LatLng(lat, lng)
        if (!mMarkers.containsKey(key)) {
            mMarkers[key!!] = mMap!!.addMarker(
                MarkerOptions().title("Your Order")
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_delivery_man))
                    .position(location)
            )
        } else {
            mMarkers[key]?.setPosition(location)
        }
        val builder = LatLngBounds.Builder()
        for (marker in mMarkers.values) {
            builder.include(marker.position)
        }
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300))

    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_delivery_man)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20
        )
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun generateRouteOnMap(result: List<List<HashMap<String, String>>>) {
        SnapLog.print("generate route======")
        var points: ArrayList<LatLng>? = null
        var lineOptions: PolylineOptions? = null
        val markerOptions = MarkerOptions()

        for (element in result) {
            points = ArrayList()
            lineOptions = PolylineOptions()
            val path: List<HashMap<String, String>> = element
            for (j in path.indices) {
                val point: HashMap<String, String> = path[j]
                val lat: Double = point.get("lat")!!.toDouble()
                val lng: Double = point.get("lng")!!.toDouble()
                val position = LatLng(lat, lng)
                points.add(position)
            }
            lineOptions.addAll(points)
            lineOptions.width(12f)
            lineOptions.color(Color.BLACK)
            lineOptions.geodesic(true)
        }
        // Drawing polyline in the Google Map for the i-th route
        mMap!!.addPolyline(lineOptions)

    }


}