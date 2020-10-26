package com.ezymd.restaurantapp.ui.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.location.LocationRepository
import com.ezymd.restaurantapp.location.model.LocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var loginRepository: HomeRepository? = null
    val address: MutableLiveData<LocationModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = HomeRepository.instance
        address = MutableLiveData()
        isLoading = MutableLiveData()


    }


    fun getAddress(locationModel: LocationModel, gcd: Geocoder) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository!!.getAddress(gcd, locationModel, address, isLoading)
        }

    }

}