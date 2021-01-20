package com.ezymd.restaurantapp.cart

import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.cart.model.TransactionChargeModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.BaseResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class CartRepository {


    suspend fun startCheckoutPayment(
        jsonObject: JsonObject,
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<BaseResponse> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.startCheckout(
                jsonObject.asJsonObject, baseRequest.accessToken
            )
        }


    }

    suspend fun getTransactionCharges(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<TransactionChargeModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.transactionCharge(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    suspend fun createCustomer(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<JsonObject> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.createCustomer(
                baseRequest.accessToken
            )
        }


    }

    suspend fun locationValidate(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationValidatorModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.locationValidate(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    suspend fun savePayment(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationValidatorModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.savePaymentInfo(
                baseRequest.paramsMap, baseRequest.accessToken
            )
        }


    }


    suspend fun saveCodPayment(
        jsonObj: JsonObject,
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationValidatorModel> {

        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val body: RequestBody =
            jsonObj.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.saveCodPayment(
                body, baseRequest.accessToken
            )
        }


    }

    companion object {
        @Volatile
        private var sportsFeeRepository: CartRepository? = null

        @JvmStatic
        val instance: CartRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(CartRepository::class.java) {
                        sportsFeeRepository = CartRepository()
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