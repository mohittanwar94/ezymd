package com.ezymd.restaurantapp.cart

import com.ezymd.restaurantapp.cart.model.TransactionChargeModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class OrderStatusRepository {


    suspend fun getOrderStatus(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<TransactionChargeModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.transactionCharge(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: OrderStatusRepository? = null

        @JvmStatic
        val instance: OrderStatusRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(OrderStatusRepository::class.java) {
                        sportsFeeRepository = OrderStatusRepository()
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