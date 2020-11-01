package com.ezymd.restaurantapp.editprofile

import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.login.LoginRequest
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.CoroutineDispatcher
import java.util.HashMap

class EditProfileRepository {


    suspend fun generateOtp(
        otp: String,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<OtpModel> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val map = HashMap<String, String>()
        map.put("phone_no", otp)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.sendOtp(
                map
            )
        }


    }


    suspend fun updateUprofile(
        loginRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LoginModel> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.loginSocialUser(
                loginRequest.paramsMap
            )
        }


    }


    companion object {
        @Volatile
        private var sportsFeeRepository: EditProfileRepository? = null

        @JvmStatic
        val instance: EditProfileRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(EditProfileRepository::class.java) {
                        sportsFeeRepository = EditProfileRepository()
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