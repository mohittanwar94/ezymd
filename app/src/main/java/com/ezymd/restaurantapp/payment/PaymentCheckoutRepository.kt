package com.ezymd.restaurantapp.payment

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.BaseResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher

class PaymentCheckoutRepository {


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


    companion object {
        @Volatile
        private var sportsFeeRepository: PaymentCheckoutRepository? = null

        @JvmStatic
        val instance: PaymentCheckoutRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(PaymentCheckoutRepository::class.java) {
                        sportsFeeRepository = PaymentCheckoutRepository()
                    }
                }
                return sportsFeeRepository
            }
    }


}