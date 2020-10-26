package com.ezymd.restaurantapp.location

import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.location.model.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private var loginRepository: LocationRepository? = null
    val address: MutableLiveData<LocationModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = LocationRepository.instance
        address = MutableLiveData()
        isLoading = MutableLiveData()


    }


    fun getAddress(lat: Double, long: Double, gcd: Geocoder) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository!!.getAddress(gcd, lat, long, address, isLoading)
        }

    }


}