package com.ezymd.restaurantapp.refer

import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class ReferRepository {


    suspend fun resturantDetails(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<MenuItemModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.getResturantDetails(
                baseRequest.paramsMap.get("id")!!.toInt(), baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: ReferRepository? = null

        @JvmStatic
        val instance: ReferRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(ReferRepository::class.java) {
                        sportsFeeRepository = ReferRepository()
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