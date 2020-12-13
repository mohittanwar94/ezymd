package com.ezymd.restaurantapp.cart

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.BaseResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher

class CartRepository {


    suspend fun startCheckoutPayment(
        jsonObject: JsonObject,
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<BaseResponse> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.startCheckout(
                jsonObject.asJsonObject,baseRequest.accessToken
            )
        }


    }

    suspend fun createCustomer(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<JsonObject> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.createCustomer(
                baseRequest.accessToken
            )
        }


    }


    suspend fun getRestaurants(
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

    companion object {
        @Volatile
        private var sportsFeeRepository: CartRepository? = null

        @JvmStatic
        val instance: CartRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(CartRepository::class.java) {
                        sportsFeeRepository = CartRepository()
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