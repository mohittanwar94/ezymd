package com.ezymd.restaurantapp.network


import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.ui.home.model.TrendingModel
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


    @FormUrlEncoded
    @POST(ServerConfig.LOGIN_USER)
    suspend fun loginUser(
        @FieldMap commonParameters: Map<String, String>
    ): LoginModel

    @FormUrlEncoded
    @POST(ServerConfig.LIST_BANNER)
    suspend fun listBanners(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): ResturantModel


    @FormUrlEncoded
    @POST(ServerConfig.LIST_RESTURANTS)
    suspend fun getResturants(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): ResturantModel

    @FormUrlEncoded
    @POST(ServerConfig.LIST_TRENDING)
    suspend fun getTrending(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): TrendingModel

    @FormUrlEncoded
    @POST(ServerConfig.SOCIAL_LOGIN_USER)
    suspend fun loginSocialUser(
        @FieldMap commonParameters: Map<String, String>
    ): LoginModel

}

