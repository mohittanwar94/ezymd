package com.ezymd.restaurantapp.location

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import java.io.IOException


class LocationRepository private constructor() {


    fun getAddress(
        gcd: Geocoder,
        lat: Double,
        long: Double,
        addressResult: MutableLiveData<String>,
        isLoading: MutableLiveData<Boolean>
    ) {
        var address = "No known address"


        val addresses: List<Address>
        try {
            addresses = gcd.getFromLocation(
               lat,
               long,
                1
            )
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
            }
            addressResult.postValue(address)
            isLoading.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
            addressResult.postValue(address)
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
