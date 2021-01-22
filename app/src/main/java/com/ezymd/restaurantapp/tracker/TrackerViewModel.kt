package com.ezymd.restaurantapp.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.tracker.model.BaseUpdateLocationModel
import com.ezymd.restaurantapp.utils.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class TrackerViewModel : ViewModel() {
    var errorRequest: SingleLiveEvent<String> = SingleLiveEvent()
    private var loginRepository: TrackerRepository? = null
    val routeInfoResponse: MutableLiveData<ArrayList<List<HashMap<String, String>>>>
    val timeInfoResponse= MutableLiveData<ArrayList<List<HashMap<String, String>>>>()
    val firebaseResponse: MutableLiveData<DataSnapshot>
    val locationUpdate = MutableLiveData<BaseUpdateLocationModel>()
    val orderCancel = MutableLiveData<LocationValidatorModel>()
    val isLoading: MutableLiveData<Boolean>
    val timer = Timer()

    override fun onCleared() {
        timer.cancel()
        super.onCleared()
        viewModelScope.cancel()
    }

    fun isLoading(): LiveData<Boolean?> {
        return isLoading
    }

    fun startLoading(isLoadingValue: Boolean) {
        isLoading.value = isLoadingValue
    }

    init {
        loginRepository = TrackerRepository.instance
        firebaseResponse = MutableLiveData()
        isLoading = MutableLiveData()
        routeInfoResponse = MutableLiveData()
    }


    fun startTimer(order_id: String, userInfo: UserInfo) {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val baseRequest = BaseRequest(userInfo)
                baseRequest.paramsMap["id"] = order_id
                downloadLatestCoordinates(baseRequest)

            }


        }, 100, 15000)
    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }

    fun showError() = errorRequest

    fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }


    fun loginToFirebase(email: String, password: String, path: String) {
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                subscribeToUpdates(path)
                SnapLog.print("firebase auth success")
            } else {
                SnapLog.print("firebase auth failed")
            }
        };
    }

    private fun subscribeToUpdates(path: String) {
        val ref = FirebaseDatabase.getInstance().getReference(path)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                firebaseResponse.postValue(dataSnapshot)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                firebaseResponse.postValue(dataSnapshot)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {
                SnapLog.print("Failed to read value." + error.toException())
            }
        })
    }


    fun getDirectionsUrl(
        origin: LatLng,
        wayPoints: LatLng,
        dest: LatLng,
        key: String
    ): ConcurrentHashMap<String, String> {
        val haspMap = ConcurrentHashMap<String, String>()
        val str_origin = "" + origin.latitude + "," + origin.longitude
        val str_dest = "" + dest.latitude + "," + dest.longitude


        haspMap.put("waypoints", "via:" + wayPoints.latitude + "," + wayPoints.longitude)
        haspMap.put("origin", str_origin)
        haspMap.put("destination", str_dest)
        haspMap.put("sensor", "false")
        haspMap.put("mode", "driving")
        haspMap.put("key", key)

        return haspMap
    }


    fun downloadLatestCoordinates(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.updateCoordinates(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print(result.value.toString())
                    locationUpdate.postValue(result.value)
                }
            }
        }

    }

    fun downloadRoute(url: ConcurrentHashMap<String, String>) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.downloadRouteInfo(
                url,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print(result.value.toString())
                    routeInfoResponse.postValue(parseResponse(result.value.toString()))
                }
            }
        }

    }

    fun cancelOrder(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.cancelOrder(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    orderCancel.postValue(result.value)
                }
            }
        }

    }

    private fun parseResponse(value: String): ArrayList<List<HashMap<String, String>>> {
        val routes = ArrayList<List<HashMap<String, String>>>()
        try {
            val jsonObject = JSONObject(value)
            val parser = DirectionsJSONParser()
            routes.addAll(parser.parse(jsonObject))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return routes


    }

    fun calculateDuration(map: ConcurrentHashMap<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.downloadRouteInfo(
                map,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print(result.value.toString())
                    timeInfoResponse.postValue(parseResponse(result.value.toString()))
                }
            }
        }

    }


}


