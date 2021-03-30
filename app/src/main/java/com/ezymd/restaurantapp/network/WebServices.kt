package com.ezymd.restaurantapp.network


import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.cart.model.TransactionChargeModel
import com.ezymd.restaurantapp.coupon.model.CoupanBaseModel
import com.ezymd.restaurantapp.dashboard.model.TrendingDashboardModel
import com.ezymd.restaurantapp.details.model.CategoriesResponse
import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.details.model.SubCategoriesResponse
import com.ezymd.restaurantapp.filters.model.FilterModel
import com.ezymd.restaurantapp.itemdetail.model.ProductDetailBaseModel
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.refer.ReferModel
import com.ezymd.restaurantapp.tracker.model.BaseUpdateLocationModel
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.ui.home.model.TrendingModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderBaseModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderSuccessModel
import com.ezymd.restaurantapp.ui.profile.LogoutModel
import com.ezymd.restaurantapp.utils.BaseResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    @POST(ServerConfig.ACCEPT_ORDER)
    suspend fun cancelOrder(
        @FieldMap commonParameters: Map<String, String>,
        @Header("Authorization") accessToken: String
    ): LocationValidatorModel

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

    /* @FormUrlEncoded
     @POST(ServerConfig.SEARCH_RESTURANTS)
     suspend fun searchRestaurant(
         @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
     ): ResturantModel

     @FormUrlEncoded
     @POST(ServerConfig.LIST_RESTURANTS)
     suspend fun getResturants(
         @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
     ): ResturantModel

 */
    @GET(ServerConfig.RESTURANT_DETAILS)
    suspend fun getResturantDetails(
        @Path("id") id: Int, @Header("Authorization") token: String
    ): MenuItemModel


    @GET(ServerConfig.REFERRAL_DETAILS)
    suspend fun getReferrList(
         @Header("Authorization") token: String
    ): ReferModel


    @FormUrlEncoded
    @POST(ServerConfig.TRENDING_STORES)
    suspend fun trendingStores(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): TrendingDashboardModel


    @FormUrlEncoded
    @POST(ServerConfig.NEAR_BY_SHOPS)
    suspend fun nearByShops(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): TrendingDashboardModel

    @FormUrlEncoded
    @POST(ServerConfig.NEAR_BY_BANNERS)
    suspend fun nearByBanners(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): TrendingDashboardModel


    @GET(ServerConfig.RESTURANT_FILTERS)
    suspend fun getFilters(
        @Header("Authorization") token: String
    ): FilterModel


    @FormUrlEncoded
    @POST(ServerConfig.LIST_TRENDING)
    suspend fun getTrending(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): TrendingModel

    @FormUrlEncoded
    @POST(ServerConfig.SAVE_REFERRAL)
    suspend fun saveReferral(
        @Field("referral") url: String, @Header("Authorization") token: String
    ): LocationValidatorModel


    @Multipart
    @POST(ServerConfig.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Part avatar: MultipartBody.Part,
        @Header("Authorization") token: String
    ): LoginModel

    @FormUrlEncoded
    @POST(ServerConfig.UPDATE_PROFILE)
    suspend fun updateWithoutImageProfile(
        @FieldMap commonParameters: Map<String, String>,
        @Header("Authorization") token: String
    ): LoginModel


    @FormUrlEncoded
    @POST(ServerConfig.SOCIAL_LOGIN_USER)
    suspend fun loginSocialUser(
        @FieldMap commonParameters: Map<String, String>
    ): LoginModel

    @POST(ServerConfig.CREATE_ORDER)
    suspend fun startCheckout(
        @Body jsonObject: JsonObject, @Header("Authorization") token: String
    ): BaseResponse


    @GET(ServerConfig.DIRECTION_API)
    suspend fun downloadRoute(
        @Query("origin") url: String,
        @Query("sensor") sensor: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String,
        @Query("waypoints") waypoints: String
    ): JsonObject

    @POST(ServerConfig.CREATE_ORDER/*"create_payment_intent"*/)
    suspend fun createPaymentIntent(
        @Body params: MutableMap<String, Any>,
        @Header("Authorization") token: String
    ): ResponseBody

    @POST("confirm_payment_intent")
    suspend fun confirmPaymentIntent(@Body params: MutableMap<String, String?>): ResponseBody

    @FormUrlEncoded
    @POST(ServerConfig.EPHEMERAL_KEYS)
    suspend fun createEphemeralKey(
        @FieldMap apiVersionMap: MutableMap<String, String>,
        @Header("Authorization") accessToken: String?
    ): ResponseBody


    @GET(ServerConfig.CREATE_CUSTOMER)
    suspend fun createCustomer(@Header("Authorization") accessToken: String?): JsonObject


    @GET(ServerConfig.CREATE_ORDER)
    suspend fun orderList(
        @Query("customer_id") customer_id: String,
        @Query("device_id") device_id: String,
        @Query("device_token") device_token: String,
        @Header("Authorization") accessToken: String
    ): OrderBaseModel

    @GET(ServerConfig.UPDATED_COORDINATES)
    suspend fun locationUpdates(
        @Path("name") id: String,
        @Header("Authorization") accessToken: String
    ): BaseUpdateLocationModel


    @FormUrlEncoded
    @POST(ServerConfig.LOCATION_VALIDATE)
    suspend fun locationValidate(
        @FieldMap apiVersionMap: Map<String, String>,
        @Header("Authorization") accessToken: String
    ): LocationValidatorModel


    @FormUrlEncoded
    @POST(ServerConfig.TRANSACTION_CHARGES)
    suspend fun transactionCharge(
        @FieldMap apiVersionMap: Map<String, String>,
        @Header("Authorization") accessToken: String
    ): TransactionChargeModel


    @FormUrlEncoded
    @POST(ServerConfig.SAVE_PAYMENT)
    suspend fun savePaymentInfo(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): OrderSuccessModel

    @POST(ServerConfig.COD_ORDER)
    suspend fun saveCodPayment(
        @Body commonParameters: RequestBody, @Header("Authorization") token: String
    ): OrderSuccessModel


    @FormUrlEncoded
    @POST(ServerConfig.SAVE_RATING)
    suspend fun saveRating(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): LocationValidatorModel

    @GET(ServerConfig.SHOP_DETAIL)
    suspend fun shopDetail(
        @Query("shop_id") id: String,
        @Header("Authorization") accessToken: String
    ): CategoriesResponse


    @GET(ServerConfig.SHOP_PRODUCT_DETAIL)
    suspend fun shopProductCategoryDetail(
        @Query("shop_id") id: String,
        @Query("category_id") category_id: String,
        @Header("Authorization") accessToken: String
    ): SubCategoriesResponse

    @FormUrlEncoded
    @POST(ServerConfig.APPLY_COUPON)
    suspend fun applyCoupon(
        @FieldMap commonParameters: Map<String, String>, @Header("Authorization") token: String
    ): LocationValidatorModel

    @GET(ServerConfig.LIST_COUPON)
    suspend fun listCoupon(
        @Path("restaurant_id") id: String,
        @Header("Authorization") accessToken: String
    ): CoupanBaseModel

    @GET(ServerConfig.PRODUCT_DETAIL)
    suspend fun productDetail(
        @Query("product_id") id: String,
        @Header("Authorization") accessToken: String
    ): ProductDetailBaseModel

}

