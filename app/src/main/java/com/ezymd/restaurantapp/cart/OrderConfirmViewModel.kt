package com.ezymd.restaurantapp.cart


import androidx.lifecycle.*
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.*
import com.google.gson.JsonObject
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.SetupIntent
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.json.JSONObject

class OrderConfirmViewModel : ViewModel() {

    var errorRequest: MutableLiveData<String>
    val dateSelected: MutableLiveData<String>
    val isNowSelectd: MutableLiveData<Boolean>
    val isCustomerIDAvailable: MutableLiveData<Boolean>
    val baseResponse: MutableLiveData<BaseResponse>

    private var loginRepository: CartRepository? = null
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
        viewModelScope.cancel()
    }


    init {

        loginRepository = CartRepository.instance
        isLoading = MutableLiveData()
        isCustomerIDAvailable = MutableLiveData()
        isCustomerIDAvailable.postValue(false)
        baseResponse = MutableLiveData()
        errorRequest = MutableLiveData()
        isNowSelectd = MutableLiveData()
        isNowSelectd.postValue(true)
        dateSelected = MutableLiveData()


    }

    fun checkout(jsonObject: JsonObject, baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.startCheckoutPayment(
                jsonObject, baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> baseResponse.postValue(result.value)
            }
        }


    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    val backendApi = ApiClient.client!!.create(WebServices::class.java)
    private lateinit var stripeObj: Stripe
    private val coroutineContext = Dispatchers.IO + SupervisorJob()

    fun createPaymentIntent(params: Map<String, Any>, accessToken: String): LiveData<Result<JSONObject>> {
        return executeBackendMethod {
            backendApi.createPaymentIntent(params.toMutableMap(),accessToken)
        }
    }


    fun confirmStripeIntent(params: Map<String, String?>): LiveData<Result<JSONObject>> {
        return executeBackendMethod {
            params.toMutableMap().let { backendApi.confirmPaymentIntent(it) }
        }
    }

    private fun executeBackendMethod(
        backendMethod: suspend () -> ResponseBody
    ) = liveData(coroutineContext) {
        emit(
            runCatching {
                JSONObject(backendMethod().string())
            }
        )
    }

    fun retrievePaymentIntent(clientSecret: String): LiveData<Result<PaymentIntent>> {
        val liveData = MutableLiveData<Result<PaymentIntent>>()
        stripeObj.retrievePaymentIntent(
            clientSecret,
            callback = object : ApiResultCallback<PaymentIntent> {
                override fun onError(e: Exception) {
                    liveData.value = Result.failure(e)
                }

                override fun onSuccess(result: PaymentIntent) {
                    liveData.value = Result.success(result)
                }
            }
        )
        return liveData
    }

    fun retrieveSetupIntent(clientSecret: String): LiveData<Result<SetupIntent>> {
        val liveData = MutableLiveData<Result<SetupIntent>>()
        stripeObj.retrieveSetupIntent(
            clientSecret,
            callback = object : ApiResultCallback<SetupIntent> {
                override fun onError(e: Exception) {
                    liveData.value = Result.failure(e)
                }

                override fun onSuccess(result: SetupIntent) {
                    liveData.value = Result.success(result)
                }
            }
        )
        return liveData
    }

    fun createPaymentMethod(params: PaymentMethodCreateParams): LiveData<Result<PaymentMethod>> {
        val liveData = MutableLiveData<Result<PaymentMethod>>()
        stripeObj.createPaymentMethod(
            params,
            callback = object : ApiResultCallback<PaymentMethod> {
                override fun onSuccess(result: PaymentMethod) {
                    liveData.value = Result.success(result)
                }

                override fun onError(e: Exception) {
                    liveData.value = Result.failure(e)
                }
            }
        )
        return liveData
    }

    fun setStripe(stripe: Stripe) {
        stripeObj = stripe

    }

    fun createCustomerStripe(userInfo: UserInfo) {
        val baseRequest = BaseRequest(userInfo)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.createCustomer(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    val id = result.value.getAsJsonObject(JSONKeys.DATA).get(JSONKeys.ID).asString
                    SnapLog.print(id)
                    userInfo.customerID = id
                    isCustomerIDAvailable.postValue(true)
                }
            }
        }

    }


}
