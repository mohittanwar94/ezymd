package com.ezymd.restaurantapp.ui.home

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.ezymd.restaurantapp.filters.model.FilterModel
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.ui.home.model.TrendingModel
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher
import java.io.IOException

class HomeRepository {

    fun getAddress(
        gcd: Geocoder,
        locationModel: LocationModel, addressResult: MutableLiveData<LocationModel>,
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
                    cityName = addresses[0].locality

            }
            locationModel.location = address
            locationModel.city = cityName
            addressResult.postValue(locationModel)
           // isLoading.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
            locationModel.location = address
            locationModel.city = cityName
            addressResult.postValue(locationModel)
          //  isLoading.postValue(false)
        }


    }
    suspend fun getFilters(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<FilterModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.getFilters(baseRequest.accessToken)
        }


    }

    suspend fun listBanners(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ResturantModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.listBanners(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }

    suspend fun getResturants(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ResturantModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.getResturants(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    suspend fun getTrending(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<TrendingModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.getTrending(
                baseRequest.paramsMap, baseRequest.accessToken
            )
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