package com.ezymd.restaurantapp.ui.search

import com.ezymd.restaurantapp.dashboard.model.TrendingDashboardModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class SearchRepository {


    suspend fun getRestaurantSearch(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<TrendingDashboardModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.nearByShops(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }

    suspend fun getRestaurants(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<TrendingDashboardModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.nearByShops(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }

    companion object {
        @Volatile
        private var sportsFeeRepository: SearchRepository? = null

        @JvmStatic
        val instance: SearchRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(SearchRepository::class.java) {
                        sportsFeeRepository = SearchRepository()
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