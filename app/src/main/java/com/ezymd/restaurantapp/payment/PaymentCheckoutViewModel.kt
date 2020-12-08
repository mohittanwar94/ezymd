package com.ezymd.restaurantapp.payment


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.BaseResponse
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PaymentCheckoutViewModel : ViewModel() {

    var errorRequest: MutableLiveData<String>
    private var loginRepository: PaymentCheckoutRepository? = null
    val isLoading: MutableLiveData<Boolean>
    val baseResponse: MutableLiveData<BaseResponse>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = PaymentCheckoutRepository.instance
        isLoading = MutableLiveData()
        errorRequest = MutableLiveData()
        baseResponse = MutableLiveData()

    }

    fun checkout(jsonObject: JsonObject, baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.startCheckoutPayment(
                jsonObject,baseRequest,
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

}
