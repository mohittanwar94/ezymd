package com.ezymd.restaurantapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.font.Sizes
import com.ezymd.restaurantapp.push.SinchService
import com.ezymd.restaurantapp.push.SinchService.SinchServiceInterface
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import java.util.*


open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener,
    ServiceConnection {

    var mSinchServiceInterface: SinchServiceInterface? = null
    private var size: Sizes? = null
    val PERMISSIONS_REQUEST_CAMERA = 3333
    val PERMISSIONS_REQUEST_CAMERA_AUDIO = 3331
    val PERMISSIONS_REQUEST_READ_WRITE = 4444
    val PERMISSIONS_REQUEST_AUDIO = 5555
    val REQUEST_RECORD_AUDIO = 1002
    val AppPicker = 1113
    val PERMISSIONS_REQUEST_LOCATION = 66666
    val PERMISSIONS_REQUEST_CALL = 66667
    private var isBackground = true
    var userInfo: UserInfo? = null
    var alertDialog: ShowDialog? = null
    protected var mActivity: Context? = null
    private var permissionListener: PermissionListener? = null
    private var isEventEnabled = true
    private var snackbar: Snackbar? = null
    private var netWorkChange: Snackbar? = null
    private var mLastTimeNoInternet = System.currentTimeMillis()
    private var handler: Handler? = null


    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder?) {
        if ((SinchService::class.java.name == componentName.className)) {
            mSinchServiceInterface = iBinder as SinchServiceInterface?
            onServiceConnected()
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        if ((SinchService::class.java.name == componentName.className)) {
            mSinchServiceInterface = null
            onServiceDisconnected()
        }
    }


    open fun onServiceConnected() {}
    open fun onServiceDisconnected() {}
    fun getSinchServiceInterface(): SinchServiceInterface? {
        return mSinchServiceInterface
    }

    private val messenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SinchService.MESSAGE_PERMISSIONS_NEEDED -> {
                    val bundle = msg.data
                    val requiredPermission = bundle.getString(SinchService.REQUIRED_PERMISSION)
                    ActivityCompat.requestPermissions(
                        this@BaseActivity,
                        arrayOf(requiredPermission),
                        0
                    )
                }
            }
        }
    })


    private fun bindService() {
        val serviceIntent = Intent(this, SinchService::class.java)
        serviceIntent.putExtra(SinchService.MESSENGER, messenger)
        getApplicationContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.GRAY
        }
        super.onCreate(savedInstanceState)

        bindService()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        )

        if (ServerConfig.IS_TESTING)
            Thread.setDefaultUncaughtExceptionHandler(TopExceptionHandler(this))
        mActivity = this
        CustomTypeFace.createInstance(this)
        userInfo = UserInfo.getInstance(this)
        try {
            adjustFontScale(resources.configuration)
        } catch (ignored: Exception) {
        }
        if (size == null) size = Sizes(resources.displayMetrics)
        handler = Handler(mainLooper)

    }

    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    fun getRestaurantObject(dataTrending: DataTrending) = Resturant().apply {
        address = dataTrending.address
        lat = dataTrending.lat.toDouble()
        longitude = dataTrending.lang.toDouble()
        id = dataTrending.id
        name = dataTrending.name
        banner = dataTrending.banner
        category = dataTrending.cuisines
        cuisines = dataTrending.cuisines
        rating = dataTrending.rating
        minOrder = dataTrending.min_order
        discount = dataTrending.discount.toInt()
        isFreeDelivery = dataTrending.is_free_delivery.toInt()
        distance = dataTrending.distance
        phoneNo = dataTrending.phone_no

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        onNetWorkChange(isConnected)
    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppPicker) {
            enableEvents()
        } else if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                ShowDialog(mActivity).disPlayDialog(
                    "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.",
                    false,
                    false
                )
            } else {
                handleDriveAccounts()
            }
        } else if (requestCode == REQUEST_ACCOUNT_PICKER) {
            if (resultCode == RESULT_OK && data != null && data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    userInfo.driveAccount(accountName)
                    userInfo.driveAccountDate("" + System.currentTimeMillis())
                    mCredential.setSelectedAccountName(accountName)
                    handleDriveAccounts()
                }
            }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                handleDriveAccounts()
            }
        }
    }

*/
    override fun onResume() {
        super.onResume()
        EzymdApplication.getInstance().setConnectivityListener(this)

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        try {
            EzymdApplication.getInstance().setConnectivityListener(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onStop()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        SuspendKeyPad.suspendKeyPad(mActivity)
        return super.onTouchEvent(event)
    }

    fun checkLocationPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    mActivity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_LOCATION)
                    return false
                }
            }
        }
        return true
    }

    fun checkCallPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.CALL_PHONE)
                requestPermissions(permissions, PERMISSIONS_REQUEST_CALL)
                return false
            }
        }
        return true
    }

    override fun onBackPressed() {
        try {
            if (isEventEnabled) super.onBackPressed()
        } catch (ignored: Exception) {
        }
    }

    fun disableEvents() {
        isEventEnabled = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun enableEvents() {
        isEventEnabled = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showPermissionAlert(msg: String) {
        AlertDialog.Builder(mActivity!!)
            .setMessage(msg)
            .setPositiveButton("OK") { dialog, which ->
                val myAppSettings = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (myAppSettings.resolveActivity(packageManager) != null) startActivity(
                    myAppSettings
                )
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    fun checkCameraPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_CAMERA)
                    return false
                }
            }
        }
        return true
    }

    fun checkCameraAudioPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_CAMERA_AUDIO)
                    return false
                }
            }
        }
        return true
    }

    fun checkPhoneAudioPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions =
                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO)
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                        notGranted.add(permission)
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_CAMERA_AUDIO)
                    return false
                }
            }
        }
        return true
    }


    fun checkReadWritePermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_READ_WRITE)
                    return false
                }
            }
        }
        return true
    }

    fun checkAudioPermissions(listener: PermissionListener): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionListener = listener
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(
                    mActivity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                        != PackageManager.PERMISSION_GRANTED) || checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                val notGranted = ArrayList<String>()
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                for (permission in permissions) {
                    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) notGranted.add(
                        permission
                    )
                }
                if (!notGranted.isEmpty()) {
                    requestPermissions(notGranted.toTypedArray(), PERMISSIONS_REQUEST_AUDIO)
                    return false
                }
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionListener == null || permissions == null || grantResults == null) return
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA, PERMISSIONS_REQUEST_READ_WRITE -> if (grantResults.size > 0) {
                if (isGrant(grantResults)) permissionListener!!.result(true) else {
                    permissionListener!!.result(false)
                    showPermissionAlert("Media Files access denied.Please enable permissions to access.")
                }
            } else {
                permissionListener!!.result(false)
                showPermissionAlert("Media Files access denied.Please enable permissions to access.")
            }
            PERMISSIONS_REQUEST_CAMERA_AUDIO -> if (grantResults.size > 0) {
                if (isGrant(grantResults)) permissionListener!!.result(true) else {
                    permissionListener!!.result(false)
                    showPermissionAlert("Camera & Audio access denied.Please enable permissions to access.")
                }
            } else {
                permissionListener!!.result(false)
                showPermissionAlert("Camera & Audio access denied.Please enable permissions to access.")
            }
            0 -> if (grantResults.size > 0) {
                if (isGrant(grantResults))
                    mSinchServiceInterface?.retryStartAfterPermissionGranted()
                else {
                    mSinchServiceInterface?.retryStartAfterPermissionGranted()
                    showPermissionAlert("Camera & Audio access denied.Please enable permissions to access.")
                }
            } else {
                Toast.makeText(
                    this,
                    "Ezymd application needs permission to use your microphone and camera to function properly.",
                    Toast.LENGTH_LONG
                ).show()
            }
            PERMISSIONS_REQUEST_AUDIO -> if (grantResults.size > 0) {
                if (isGrant(grantResults)) permissionListener!!.result(true) else {
                    permissionListener!!.result(false)
                    ShowDialog(mActivity).disPlayDialog(R.string.camera_permisssion, false, false)
                }
            } else {
                permissionListener!!.result(false)
                ShowDialog(mActivity).disPlayDialog(R.string.camera_permisssion, false, false)
            }
            PERMISSIONS_REQUEST_CALL -> if (grantResults.size < 1 || !isGrant(grantResults)) {
                permissionListener!!.result(false)
                ShowDialog(mActivity).disPlayDialog(R.string.call_permisssion, false, false)
            } else permissionListener!!.result(true)
            PERMISSIONS_REQUEST_LOCATION -> if (grantResults.size > 0) {
                if (isGrant(grantResults)) permissionListener!!.result(true) else {
                    permissionListener!!.result(false)
                }
            } else {
                permissionListener!!.result(false)
            }
        }
    }


    private fun isGrant(grantResults: IntArray): Boolean {
        for (i in grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    @SuppressLint("InflateParams")
    fun onNetWorkChange(isNetworkAvailable: Boolean) {
        if (netWorkChange != null && netWorkChange!!.isShown) netWorkChange!!.dismiss()
        if (Math.abs(System.currentTimeMillis() - mLastTimeNoInternet) < 3000)
            return
        try {
            if (!isNetworkAvailable) {
                mLastTimeNoInternet = System.currentTimeMillis()
                netWorkChange = Snackbar
                    .make(
                        (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0),
                        "",
                        Snackbar.LENGTH_INDEFINITE
                    )

                val customSnackView: View =
                    layoutInflater.inflate(R.layout.custom_snackbar_view, null)
                netWorkChange!!.view.setBackgroundColor(Color.TRANSPARENT)
                val textMsg: SnapTextView = customSnackView.findViewById(R.id.textView)
                textMsg.text = getString(R.string.no_internet_connection)
                val snackbarLayout = netWorkChange!!.view as SnackbarLayout
                snackbarLayout.setPadding(0, 0, 0, 0)
                snackbarLayout.addView(customSnackView, 0)

                netWorkChange?.show()

                /*val sbView = netWorkChange!!.view
                sbView.setBackgroundColor(Color.parseColor("#ff7675"))
                val textView =
                    sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen._13sdp)
                )
                textView.typeface = CustomTypeFace.book
                textView.maxLines = 5
                textView.gravity = Gravity.CENTER
                netWorkChange!!.show()*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*  fun isGooglePlayServicesAvailable(): Boolean {
          val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
          val connectionStatusCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)
          return connectionStatusCode == ConnectionResult.SUCCESS
      }

      fun acquireGooglePlayServices() {
          val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
          val connectionStatusCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)
          if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
              showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
          }
      }

      fun showGooglePlayServicesAvailabilityErrorDialog(
          connectionStatusCode: Int
      ) {
          val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
          val dialog: Dialog = apiAvailability.getErrorDialog(
              this,
              connectionStatusCode,
              REQUEST_GOOGLE_PLAY_SERVICES
          )
          dialog.show()
      }

    */


    fun showError(noError: Boolean, message: Int, callback: Snackbar.Callback?) {
        try {
            if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
            snackbar = Snackbar
                .make(
                    (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0),
                    "",
                    5000
                )
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_snackbar_view, null)
            snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
            val textMsg: SnapTextView = customSnackView.findViewById(R.id.textView)
            textMsg.text = getString(message)
            val snackbarLayout = snackbar!!.view as SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0)

            if (callback != null) snackbar!!.addCallback(callback)
            snackbar!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showError(view: View?, noError: Boolean, message: Int) {
        try {
            if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
            snackbar = Snackbar.make(view!!, "", 5000)
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_snackbar_view, null)
            snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
            val textMsg: SnapTextView = customSnackView.findViewById(R.id.textView)
            textMsg.text = getString(message)
            val snackbarLayout = snackbar!!.view as SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0)
            snackbar!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showError(view: View?, noError: Boolean, message: String?) {
        try {
            if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
            snackbar = Snackbar.make(view!!, "", 5000)
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_snackbar_view, null)
            snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
            val textMsg: SnapTextView = customSnackView.findViewById(R.id.textView)
            textMsg.text = message
            val snackbarLayout = snackbar!!.view as SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0)


            snackbar!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showError(noError: Boolean, message: String?, callback: Snackbar.Callback?) {
        try {
            if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
            snackbar = Snackbar
                .make(
                    (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0),
                    "", 5000
                )
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_snackbar_view, null)
            val textMsg: SnapTextView = customSnackView.findViewById(R.id.textView)
            textMsg.text = message
            snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
            val snackbarLayout = snackbar!!.view as SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0)

            if (callback != null) snackbar!!.addCallback(callback)
            snackbar!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface PermissionListener {
        fun result(isGranted: Boolean)
    }


}