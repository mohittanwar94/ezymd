package com.ezymd.restaurantapp.review

import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.CoroutineDispatcher


class ReviewRepository private constructor() {



    suspend fun saveRating(
        loginRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationValidatorModel> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.saveRating(
                loginRequest.paramsMap,loginRequest.accessToken
            )
        }


    }



    companion object {
        @Volatile
        private var sportsFeeRepository: ReviewRepository? = null

        @JvmStatic
        val instance: ReviewRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(ReviewRepository::class.java) {
                        sportsFeeRepository = ReviewRepository()
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
