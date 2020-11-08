package com.ezymd.restaurantapp.filters

import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.filters.model.FilterModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class FilterRepository {


    suspend fun getFilters(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<FilterModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.getFilters(baseRequest.accessToken)
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: FilterRepository? = null

        @JvmStatic
        val instance: FilterRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(FilterRepository::class.java) {
                        sportsFeeRepository = FilterRepository()
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