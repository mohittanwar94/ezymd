package com.ezymd.restaurantapp.login.otp

import com.ezymd.restaurantapp.login.LoginRequest
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

class OtpRepository private constructor() {


    fun startSmsListener(client: SmsRetrieverClient) {
        val task = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid: Void? ->
            SnapLog.print(
                "started sms client"
            )
        }
        task.addOnFailureListener { e: Exception -> SnapLog.print("failed sms client:" + e.message) }
    }

    suspend fun resendSms(
        mobile: String,
        countryCode: String,
        dispatcher: CoroutineDispatcher,
        hasExtra: Boolean
    ): ResultWrapper<OtpModel> {
        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val map = HashMap<String, String>()
        map["phone_no"] = mobile
        map["country_code"] = countryCode
        if (hasExtra)
            map["is_otp"] = "1"
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.sendOtp(map)
        }
    }


    suspend fun registerLoginUser(
        loginRequest: LoginRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LoginModel> {
        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val map = HashMap<String, String>()
        map.put("phone_no", loginRequest.mobileNo)
        map.put("otp", loginRequest.otp)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.loginUser(map)
        }
    }

    companion object {
        @Volatile
        private var sportsFeeRepository: OtpRepository? = null

        @JvmStatic
        val instance: OtpRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(OtpRepository::class.java) {
                        sportsFeeRepository = OtpRepository()
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
