package com.ezymd.restaurantapp.ui.profile

import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import kotlinx.coroutines.CoroutineDispatcher

class ProfileRepository {


    suspend fun logout(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LogoutModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.logout(
                baseRequest.accessToken
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: ProfileRepository? = null

        @JvmStatic
        val instance: ProfileRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(ProfileRepository::class.java) {
                        sportsFeeRepository = ProfileRepository()
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