package com.ezymd.restaurantapp.orderdetails

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.refer.ReferModel
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class OrderDetailRepository {


    suspend fun emailInvoice(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ReferModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.emailInvoice(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: OrderDetailRepository? = null

        @JvmStatic
        val instance: OrderDetailRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(OrderDetailRepository::class.java) {
                        sportsFeeRepository = OrderDetailRepository()
                    }
                }
                return sportsFeeRepository
            }
    }


}