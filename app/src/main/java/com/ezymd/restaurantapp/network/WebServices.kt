package com.ezymd.restaurantapp.network


import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.ui.home.model.TrendingModel
import com.ezymd.restaurantapp.ui.profile.LogoutModel
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

    @GET(ServerConfig.LOGOUT)
    suspend fun logout(
        @Header("Authorization") token: String
    ): LogoutModel

    @FormUrlEncoded
    @POST(ServerConfig.LIST_RESTURANTS)
    suspend fun getResturants(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): ResturantModel

    @GET(ServerConfig.RESTURANT_DETAILS)
    suspend fun getResturantDetails(
        @Path("id") id: Int, @Header("Authorization") token: String
    ): MenuItemModel

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

