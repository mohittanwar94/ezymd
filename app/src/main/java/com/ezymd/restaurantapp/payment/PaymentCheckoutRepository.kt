package com.ezymd.restaurantapp.payment

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.refer.ReferModel
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class PaymentCheckoutRepository {


    suspend fun startCheckoutPayment(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ReferModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.balanceWallet(
                baseRequest.paramsMap, baseRequest.accessToken
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