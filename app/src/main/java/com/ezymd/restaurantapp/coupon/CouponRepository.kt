package com.ezymd.restaurantapp.coupon

import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.CoroutineDispatcher


class CouponRepository private constructor() {


    suspend fun listCoupon(
        loginRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationValidatorModel> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.listCoupon(
                loginRequest.paramsMap.get("id")!!, loginRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: CouponRepository? = null

        @JvmStatic
        val instance: CouponRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(CouponRepository::class.java) {
                        sportsFeeRepository = CouponRepository()
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
