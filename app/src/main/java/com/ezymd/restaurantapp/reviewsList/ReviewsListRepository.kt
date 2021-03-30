package com.ezymd.restaurantapp.reviewsList

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.reviewsList.model.ShopReviewsBaseModel
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher


class ReviewsListRepository private constructor() {


    suspend fun getShopReview(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<ShopReviewsBaseModel> {

        val apiServices = ApiClient.client?.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices!!.getShopReview(
                baseRequest.paramsMap["shop_id"]!!, baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: ReviewsListRepository? = null

        @JvmStatic
        val instance: ReviewsListRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(ReviewsListRepository::class.java) {
                        sportsFeeRepository = ReviewsListRepository()
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
