package com.ezymd.restaurantapp.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SingleLiveEvent
import com.ezymd.restaurantapp.utils.SnapLog
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
    var errorRequest: SingleLiveEvent<String>
    private var loginRepository: TrackerRepository? = null
    val routeInfoResponse: MutableLiveData<ArrayList<List<HashMap<String, String>>>>
    val firebaseResponse: MutableLiveData<DataSnapshot>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
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
        errorRequest = SingleLiveEvent()
        loginRepository = TrackerRepository.instance
        firebaseResponse = MutableLiveData()
        isLoading = MutableLiveData()
        routeInfoResponse = MutableLiveData()
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
        dest: LatLng,
        key: String
    ): ConcurrentHashMap<String, String> {
        val haspMap = ConcurrentHashMap<String, String>()
        val str_origin = "" + origin.latitude + "," + origin.longitude
        val str_dest = "" + dest.latitude + "," + dest.longitude


        haspMap.put("origin", str_origin)
        haspMap.put("destination", str_dest)
        haspMap.put("sensor", "false")
        haspMap.put("mode", "driving")
        haspMap.put("key", key)

        return haspMap
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

    private fun parseResponse(value: String): ArrayList<List<HashMap<String, String>>> {
        val routes = ArrayList<List<HashMap<String, String>>>()
        try {
            val jsonObject=JSONObject(value)
            val parser = DirectionsJSONParser()
            routes.addAll(parser.parse(jsonObject))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return routes


    }


}


