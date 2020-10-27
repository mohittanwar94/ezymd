package com.ezymd.restaurantapp.ui.home

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.ezymd.restaurantapp.location.LocationRepository
import com.ezymd.restaurantapp.location.model.LocationModel
import java.io.IOException

class HomeRepository {

    fun getAddress(
        gcd: Geocoder,
        locationModel: LocationModel
        ,addressResult: MutableLiveData<LocationModel>,
        isLoading: MutableLiveData<Boolean>
    ) {
        var address = "No known address"
        var cityName = "N/A"


        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
                locationModel.lat,
                locationModel.lang,
                1
            )
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
                if (addresses[0].getLocality() != null)
                    cityName = addresses[0].getLocality()

            }
            locationModel.location=address
            locationModel.city=cityName
            addressResult.postValue(locationModel)
            isLoading.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
            locationModel.location=address
            locationModel.city=cityName
            addressResult.postValue(locationModel)
            isLoading.postValue(false)
        }


    }

    companion object {
        @Volatile
        private var sportsFeeRepository: HomeRepository? = null

        @JvmStatic
        val instance: HomeRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(HomeRepository::class.java) {
                        sportsFeeRepository = HomeRepository()
                    }
                }
                return sportsFeeRepository
            }
    }

    init {
        if (sportsFeeRepository != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

}