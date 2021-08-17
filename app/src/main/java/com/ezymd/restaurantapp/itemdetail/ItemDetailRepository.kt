package com.ezymd.restaurantapp.itemdetail

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.ezymd.restaurantapp.itemdetail.model.ProductDetailBaseModel
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher
import java.io.IOException


class ItemDetailRepository private constructor() {


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
        private var sportsFeeRepository: ItemDetailRepository? = null

        @JvmStatic
        val instance: ItemDetailRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(ItemDetailRepository::class.java) {
                        sportsFeeRepository = ItemDetailRepository()
                    }
                }
                return sportsFeeRepository
            }
    }

    suspend fun getProductDetailsData(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ProductDetailBaseModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.productDetail(
                baseRequest.paramsMap["product_id"]!!, baseRequest.accessToken
            )
        }


    }
    init {
        if (sportsFeeRepository != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }
}
