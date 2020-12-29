package com.ezymd.restaurantapp.ui.myorder

import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderBaseModel
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.BaseResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher

class OrderListRepository {



    suspend fun listOrders(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<OrderBaseModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.orderList(
            baseRequest.paramsMap["customer_id"]!!,
                baseRequest.accessToken
            )
        }


    }



    companion object {
        @Volatile
        private var sportsFeeRepository: OrderListRepository? = null

        @JvmStatic
        val instance: OrderListRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(OrderListRepository::class.java) {
                        sportsFeeRepository = OrderListRepository()
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