package com.ezymd.restaurantapp.location

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.ezymd.restaurantapp.location.model.LocationModel
import java.io.IOException


class LocationRepository private constructor() {


    fun getAddress(
        gcd: Geocoder,
        lat: Double,
        long: Double,
        addressResult: MutableLiveData<LocationModel>,
        isLoading: MutableLiveData<Boolean>
    ) {
        var address = "No known address"
        var cityName = "N/A"

        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
                lat,
                long,
                1
            )
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
                if (addresses[0].getLocality() != null)
                    cityName = addresses[0].getLocality()

            }
            val locationModel = LocationModel()
            locationModel.lang = long
            locationModel.lat = lat
            locationModel.location = address
            locationModel.city = cityName
            addressResult.postValue(locationModel)
            isLoading.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
            val locationModel = LocationModel()
            locationModel.lang = long
            locationModel.lat = lat
            locationModel.city = cityName
            locationModel.location = address
            addressResult.postValue(locationModel)
            isLoading.postValue(false)
        }

    }

    companion object {
        @Volatile
        private var sportsFeeRepository: LocationRepository? = null

        @JvmStatic
        val instance: LocationRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(LocationRepository::class.java) {
                        sportsFeeRepository = LocationRepository()
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
