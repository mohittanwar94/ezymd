package com.ezymd.restaurantapp.details

import com.ezymd.restaurantapp.details.model.CategoriesResponse
import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.details.model.SubCategoriesResponse
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class CategoryRepository {


    suspend fun shopDetails(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<CategoriesResponse> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.shopDetail(
                baseRequest.paramsMap["shop_id"]!!, baseRequest.accessToken
            )
        }


    }


    suspend fun shopProductCategoryDetails(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<SubCategoriesResponse> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.shopProductCategoryDetail(
                baseRequest.paramsMap["shop_id"]!!,baseRequest.paramsMap["category_id"]!!, baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: CategoryRepository? = null

        @JvmStatic
        val instance: CategoryRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(CategoryRepository::class.java) {
                        sportsFeeRepository = CategoryRepository()
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