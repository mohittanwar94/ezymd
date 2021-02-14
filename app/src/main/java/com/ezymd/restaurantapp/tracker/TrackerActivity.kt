package com.ezymd.restaurantapp.tracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.push.CallScreenActivity
import com.ezymd.restaurantapp.push.SinchService
import com.ezymd.restaurantapp.tracker.model.UpdateLocationModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderStatus
import com.ezymd.restaurantapp.utils.*
import com.ezymd.restaurantapp.utils.SupportChat.Companion.startChatSupport
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil.computeHeading
import com.sinch.android.rtc.SinchError
import kotlinx.android.synthetic.main.tracker_activity.*
import kotlinx.android.synthetic.main.user_live_tracking.*


class TrackerActivity : BaseActivity(), OnMapReadyCallback, SinchService.StartFailedListener {
    private var countTimer: CountDownTimer? = null
    private var cancelCountTimer: CountDownTimer? = null
    private var duration: String = "0"
    private lateinit var defaultLocation: LatLng
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null
    val pointsList = ArrayList<LatLng>()

    private val item by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as OrderModel
    }

    private val mMarkers: HashMap<String, Marker> = HashMap()
    private var mMap: GoogleMap? = null
    private val trackViewModel by lazy { ViewModelProvider(this).get(TrackerViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tracker_activity)
        setGUI()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


    }


    @SuppressLint("SetTextI18n")
    private fun setGUI() {

        chatLay.setOnClickListener {
            UIUtil.clickAlpha(it)
            startChatSupport(this, userInfo!!)
        }
        order_id.text = getString(R.string.orderID) + " #" + item.orderId
        order_info.text =
            TimeUtils.getReadableDate(item.created) + " | " + item.orderItems.size + " items | " + getString(
                R.string.dollor
            ) + item.total

        setOrderStatus()
        leftIcon.setOnClickListener {
            onBackPressed()
        }


        checkCancelTimer()
    }

    private fun checkCancelTimer() {
        SnapLog.print("order status==========" + item.orderStatus)
        if (TimeUtils.isOrderLive(
                item.cancel_time
            ) && item.orderStatus < OrderStatus.ORDER_ACCEPT_DELIVERY_BOY
        ) {
            cancelOrder.visibility = View.VISIBLE
            view2.visibility = View.VISIBLE
            val duration = TimeUtils.getDuration(item.cancel_time)
            SnapLog.print("cancel time=====" + duration)
            progressCancel.progressMax = duration.toFloat()
            startCancelTimer(duration)


            cancelOrder.setOnClickListener {
                UIUtil.clickAlpha(it)
                showConfirmationDialog()

            }
        }

    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this, R.style.alert_dialog_theme)
        builder.setMessage("Do you want to cancel this order?")
            .setCancelable(false)
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    dialog?.dismiss()
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap["order_id"] = "" + item.orderId
                    baseRequest.paramsMap["order_status"] = "" + OrderStatus.ORDER_CANCEL
                    trackViewModel.cancelOrder(baseRequest)
                    progressBarCancel.visibility = View.VISIBLE
                }
            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.dismiss()

                }
            })
        val alert: AlertDialog = builder.create()
        alert.setTitle("Cancel Order")
        alert.show()

    }

    private fun startCancelTimer(duration: Long) {
        cancelCountTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressCancel.progress = (duration - millisUntilFinished).toFloat()


            }

            override fun onFinish() {
                view2.visibility = View.GONE
                cancelOrder.visibility = View.GONE
            }
        }.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun setOrderStatus() {
        if (item.orderPickupStatus == JSONKeys.FROM_RESTAURANT && item.orderStatus == OrderStatus.ORDER_COMPLETED) {
            liveStatus.text = getString(R.string.your_order_is_completed)
            deliveyLay.visibility = View.GONE
            view.visibility = View.GONE
            call.visibility = View.GONE
            userImage.visibility = View.GONE
            userDetails.text = getString(R.string.go_and_pick_your_order)
        } else if (item.orderPickupStatus == JSONKeys.FROM_RESTAURANT && item.orderStatus == OrderStatus.PROCESSING) {
            liveStatus.text = getString(R.string.your_order_processing)
        } else {


            setOrderStatusDelivery()
        }
    }

    private fun setOrderStatusDelivery() {
        if (item.orderStatus == OrderStatus.PROCESSING) {
            liveStatus.text = getString(R.string.your_order_processing)
        } else if (item.orderStatus == OrderStatus.ORDER_CANCEL) {
            liveStatus.text = getString(R.string.your_order_cancel)
            view2.visibility = View.GONE
            cancelOrder.visibility = View.GONE
        } else if (item.orderStatus == OrderStatus.ORDER_ACCEPTED) {
            liveStatus.text = getString(R.string.your_order_is_cooking)
        } else if (item.orderStatus == OrderStatus.ORDER_ACCEPT_DELIVERY_BOY) {
            deliveyLay.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            liveStatus.text =
                getString(R.string.order_accepted_by_delivery_boy) + " " + item.delivery?.name
            //  SnapLog.print("duration==" + duration)
            setDeliveryInfo()
        } else if (item.orderStatus == OrderStatus.DELIVERY_BOY_REACHED_AT_RESTAURANT) {
            deliveyLay.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            liveStatus.text =
                item.delivery?.name + " " + getString(R.string.delivery_boy_reached_at_your_location)
            setDeliveryInfo()
        } else if (item.orderStatus == OrderStatus.ITEMS_PICKED_FROM_RESTAURANT) {
            deliveyLay.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            liveStatus.text = getString(R.string.order_pick_up)
            setDeliveryInfo()
        } else {
            deliveyLay.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            liveStatus.text = getString(R.string.your_order_is_completed)
            setDeliveryInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDeliveryInfo() {
        if (item.delivery == null)
            return
        userDetails.text = item.delivery?.name

        if (item.delivery?.photo != "") {
            GlideApp.with(applicationContext)
                .load(item.delivery.photo).centerCrop().dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(userImage)
        }
        call.setOnClickListener {
            try {
                val isGranted = checkPhoneAudioPermissions(object : PermissionListener {
                    override fun result(isGranted: Boolean) {
                        if (isGranted) {
                            UIUtil.clickAlpha(it)
                            loginClicked()
                        }

                    }
                })

                if (isGranted) {
                    UIUtil.clickAlpha(it)
                    loginClicked()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onServiceConnected() {
        getSinchServiceInterface()?.setStartListener(this)
    }


    override fun onStartFailed(error: SinchError) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onStarted() {
        openPlaceCallActivity()
    }

    private fun loginClicked() {
        val userName = "" + userInfo?.userID
        if (userName != getSinchServiceInterface()?.userName) {
            getSinchServiceInterface()?.stopClient()
        }
        if (!getSinchServiceInterface()?.isStarted!!) {
            getSinchServiceInterface()?.startClient(userName)
        } else {
            openPlaceCallActivity()
        }
    }

    private fun openPlaceCallActivity() {
        //item.delivery.phoneNo
        callConnect()

    }

    private fun callConnect() {
        //item.delivery.phoneNo
        val call = getSinchServiceInterface()!!.callPhoneNumber("+46000000000")
        val callId = call.callId
        val callScreen = Intent(this, CallScreenActivity::class.java)
        callScreen.putExtra(SinchService.CALL_ID, callId)
        callScreen.putExtra(JSONKeys.NAME, item.delivery.name)
        callScreen.putExtra(JSONKeys.AVATAR, item.delivery.photo)
        startActivity(callScreen)
    }


    override fun onDestroy() {
        super.onDestroy()
        cancelCountTimer?.cancel()
        countTimer?.cancel()

    }

    private fun startTimer() {
        countTimer = object : CountDownTimer((duration.toLong() * 1000), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds: Long = (millisUntilFinished / 1000)
                //SnapLog.print("seconds==" + seconds)
                val remaining = duration.toInt() - seconds.toInt()
                progress.progress = remaining

                val hours: Long = (seconds / (60 * 60))
                val minutes: Int = ((seconds % 3600) / 60).toInt()
                val second: Int = (seconds % 60).toInt()


                var time = ""
                if (hours > 0L) {
                    time = "Delivery in $hours hour: $minutes min : $second sec"
                } else {
                    if (minutes > 1)
                        time = "Delivery in $minutes mins : $second sec"
                    else
                        time = "Delivery in $minutes min : $second sec"
                }
                deliverTime.text = time

            }

            override fun onFinish() {
                deliverTime.text = getString(R.string.your_order_will_be_deliver_shortly)
            }
        }.start()
    }

    private fun setObserver() {
        //if (item.orderPickupStatus == OrderStatus.ORDER_ASSIGN_FOR_DELIVERY)
        if (item.orderStatus >= OrderStatus.ITEMS_PICKED_FROM_RESTAURANT && item.orderStatus < OrderStatus.ORDER_COMPLETED) {
            trackViewModel.startTimer(item.orderId.toString(), userInfo!!)
        } else {

            var lat = item.restaurant.lat.toDouble()
            var lng = item.restaurant.longitude.toDouble()

            val source = LatLng(lat, lng)
            lat = item.delivery_lat.toDouble()
            lng = item.delivery_lang.toDouble()

            val destination = LatLng(lat, lng)

            val hashMap = trackViewModel.getDirectionsUrl(
                source, source,
                destination,
                getString(R.string.google_maps_key)
            )
            trackViewModel.downloadRoute(hashMap)
        }

        trackViewModel.orderCancel.observe(this, Observer {
            progressBarCancel.visibility = View.GONE
            if (it != null) {
                if (it.status == ErrorCodes.SUCCESS) {
                    showError(false, it.message, null)
                    item.orderStatus = OrderStatus.ORDER_CANCEL
                    setOrderStatus()
                } else
                    showError(false, it.message, null)

            }
        })
        trackViewModel.routeInfoResponse.observe(this, Observer {
            if (it != null) {
                grayPolyline?.remove()
                blackPolyline?.remove()
                pointsList.clear()
                generateRouteOnMap(it)
                if (pointsList.size > 0)
                    showPath(pointsList)

                // showMovingCab(pointsList)
            }
        })
        trackViewModel.timeInfoResponse.observe(this, Observer {
            if (it != null) {
                for (element in it) {
                    //  points = ArrayList()
                    // lineOptions = PolylineOptions()
                    val path: List<HashMap<String, String>> = element
                    for (j in path.indices) {
                        val point: HashMap<String, String> = path[j]
                        duration = point.get("duration")!!

                        break
                    }
                }
            }
            setDuration()

        })
        trackViewModel.locationUpdate.observe(this, Observer {
            if (it != null && it.data != null) {
                //to be change
                getUpdateRoot(it.data)
            }
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
            /*  if (it != null) {
                  //   val key = it.key
                  val value = it.value as HashMap<*, *>
                  val lat = (value["latitude"].toString()).toDouble()
                  val lng = (value["longitude"].toString()).toDouble()
                  val location = LatLng(lat, lng)
                  updateCarLocation(location)
                  // setMarker(it)
              }*/
        })
    }


    private fun getUpdateRoot(data: ArrayList<UpdateLocationModel>) {
        if (data.size > 0) {
            val latLng = LatLng(data[0].lat, data[0].lang)

            if (data[0].orderStatus != item.orderStatus) {
                item.orderStatus = data[0].orderStatus
                setOrderStatus()
                if (item.orderStatus == OrderStatus.ORDER_COMPLETED)
                    progressLay.visibility = View.GONE
                EzymdApplication.getInstance().isRefresh.postValue(true)
                if (item.orderStatus >= OrderStatus.ITEMS_PICKED_FROM_RESTAURANT && item.orderStatus < OrderStatus.ORDER_COMPLETED) {
                    val lat = item.delivery_lat.toDouble()
                    val lng = item.delivery_lang.toDouble()
                    val destination = LatLng(lat, lng)
                    val map = trackViewModel.getDirectionsUrl(
                        latLng,
                        latLng,
                        destination,
                        getString(R.string.google_maps_key)
                    )
                    trackViewModel.calculateDuration(map)
                }
            }

            if (!PolyUtil.isLocationOnPath(latLng, pointsList, true, 50.0)) {
                var lat = item.restaurant.lat.toDouble()
                var lng = item.restaurant.longitude.toDouble()
                val source = LatLng(lat, lng)
                lat = item.delivery_lat.toDouble()
                lng = item.delivery_lang.toDouble()
                val destination = LatLng(lat, lng)

                val hashMap = trackViewModel.getDirectionsUrl(
                    source, latLng,
                    destination,
                    getString(R.string.google_maps_key)
                )
                trackViewModel.downloadRoute(hashMap)

                val map = trackViewModel.getDirectionsUrl(
                    latLng,
                    latLng,
                    destination,
                    getString(R.string.google_maps_key)
                )
                trackViewModel.calculateDuration(map)

            }
            if (previousLatLng == null) {
                updateCarLocation(latLng)

            } else {
                if (distanceBetween(previousLatLng!!, latLng) > 10f) {
                    updateCarLocation(latLng)

                }
            }

        }
    }

    private fun distanceBetween(latLng1: LatLng, latLng2: LatLng): Float {
        val loc1 = Location(LocationManager.GPS_PROVIDER)
        val loc2 = Location(LocationManager.GPS_PROVIDER)
        loc1.latitude = latLng1.latitude
        loc1.longitude = latLng1.longitude
        loc2.latitude = latLng2.latitude
        loc2.longitude = latLng2.longitude
        return loc1.distanceTo(loc2)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap!!.setMaxZoomPreference(20f)
        mMap!!.isTrafficEnabled = false;
        mMap!!.isIndoorEnabled = false;
        mMap!!.isBuildingsEnabled = true;
        defaultLocation = LatLng(item.delivery_lat.toDouble(), item.delivery_lang.toDouble())
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        showDefaultLocationOnMap(defaultLocation)

        /*trackViewModel.loginToFirebase(
            getString(R.string.firebase_email),
            getString(R.string.firebase_password), getString(R.string.firebase_path)
        )*/
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
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_delivery_man))
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
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val vectorDrawable =
            ContextCompat.getDrawable(this, vectorDrawableResourceId) as VectorDrawable?

        val h = vectorDrawable!!.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth

        vectorDrawable.setBounds(0, 0, w, h)

        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bm)

    }

    fun generateRouteOnMap(result: List<List<HashMap<String, String>>>) {
        SnapLog.print("generate route======")
        //  var points: ArrayList<LatLng>? = null
        // var lineOptions: PolylineOptions? = null
        //  val markerOptions = MarkerOptions()

        for (element in result) {
            //  points = ArrayList()
            // lineOptions = PolylineOptions()
            val path: List<HashMap<String, String>> = element
            for (j in path.indices) {
                val point: HashMap<String, String> = path[j]
                val lat: Double = point.get("lat")!!.toDouble()
                val lng: Double = point.get("lng")!!.toDouble()
                //duration = point.get("duration")!!
                //  SnapLog.print("duration==========" + duration)
                val position = LatLng(lat, lng)
                pointsList.add(position)
                //points.add(position)
            }
            //lineOptions.addAll(points)
            /////clineOptions.width(12f)
            // lineOptions.color(Color.BLACK)
            // lineOptions.geodesic(true)
        }
        /*if (item.orderStatus >= OrderStatus.ITEMS_PICKED_FROM_RESTAURANT && item.orderStatus < OrderStatus.ORDER_COMPLETED)
            setDuration()
*/

        // Drawing polyline in the Google Map for the i-th route
        // mMap!!.addPolyline(lineOptions)

    }

    private fun setDuration() {
        progressLay.visibility = View.VISIBLE
        // trackViewModel.calculateTime()
        if (duration.toInt() < 120) {
            duration = "120"
        }
        if (duration != "0") {
            val hours: Long = (duration.toLong() / (60 * 60))
            val minutes: Int = (duration.toInt() % 3600) / 60
            var time = ""
            if (hours > 0L) {
                time = "Delivery in $hours hour: $minutes mins"
            } else {
                if (minutes > 1)
                    time = "Delivery in $minutes mins"
                else
                    time = "Delivery in $minutes min"
            }
            progress.progress = 0
            deliverTime.text = time
            progress.max = duration.toInt()
            countTimer?.cancel()
            startTimer()
        }
    }


    private fun moveCamera(latLng: LatLng) {
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(16f).build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = MapUtils.getCarBitmap(this)
        return mMap!!.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun addOriginDestinationMarkerAndGet(isSource: Boolean, latLng: LatLng): Marker {
        val bitmapDescriptor =
            if (isSource) {
                MapUtils.getSourceBitmap(this, R.drawable.ic_user_location)
            } else {
                MapUtils.getDestinationBitmap(this, R.drawable.ic_dining_large)
            }

        return mMap!!.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun showDefaultLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    /**
     * This function is used to draw the path between the Origin and Destination.
     */
    private fun showPath(latLngList: ArrayList<LatLng>) {
        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.geodesic(true)
        polylineOptions.width(12f)
        polylineOptions.addAll(latLngList)
        grayPolyline = mMap!!.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.geodesic(true)
        blackPolylineOptions.color(ContextCompat.getColor(this, R.color.color_002366))
        blackPolylineOptions.width(12f)
        blackPolyline = mMap!!.addPolyline(blackPolylineOptions)

        originMarker = addOriginDestinationMarkerAndGet(false, latLngList[0])
        //originMarker?.setAnchor(0.5f, 0.5f)
        originMarker?.isDraggable = false
        destinationMarker = addOriginDestinationMarkerAndGet(true, latLngList[latLngList.size - 1])
        //destinationMarker?.setAnchor(0.5f, 0.5f)
        destinationMarker?.isDraggable = false

        val polylineAnimator = AnimationUtils.polylineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
            blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }

    /**
     * This function is used to update the location of the Cab while moving from Origin to Destination
     */
    private fun updateCarLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker?.position = currentLatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = AnimationUtils.carAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    val heading = computeHeading(previousLatLng, nextLocation);
                    movingCabMarker?.rotation = heading.toFloat() - 90

                    //  val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                    /* if (!rotation.isNaN()) {
                         ?.rotation = rotation
                     }*/
                    // movingCabMarker?.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }

    }


}