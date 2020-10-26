package com.ezymd.restaurantapp.network


import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.utils.BaseResponse
import retrofit2.http.*

interface WebServices {
    @FormUrlEncoded
    @POST
    suspend fun getOutStandingAmount(
        @Url url: String,
        @FieldMap commonParameters: Map<String, String>
    ): BaseResponse

    @FormUrlEncoded
    @POST(ServerConfig.GENERATE_OTP)
    suspend fun sendOtp(
        @FieldMap commonParameters: Map<String, String>
    ): OtpModel

}

