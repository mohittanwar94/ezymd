package com.ezymd.restaurantapp.editprofile

import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class EditProfileRepository {


    suspend fun generateOtp(
        otp: String, countryCode: String,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<OtpModel> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val map = HashMap<String, String>()
        map["phone_no"] = otp
        map["country_code"] = countryCode
        map["is_otp"] = "1"

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

        SnapLog.print("updateUprofile repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.updateWithoutImageProfile(
                loginRequest.paramsMap, loginRequest.accessToken
            )
        }


    }

    suspend fun updateUprofile(
        file: File,
        loginRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LoginModel> {

        SnapLog.print("updateUprofile multipart repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.updateProfile(
                MultipartBody.Part.createFormData(
                    "photo", file.name, file
                        .asRequestBody("image/*".toMediaTypeOrNull())
                ),

                loginRequest.accessToken
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