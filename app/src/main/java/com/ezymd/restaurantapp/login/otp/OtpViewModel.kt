package com.ezymd.restaurantapp.login.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.login.LoginRequest
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String>
    private var loginRepository: OtpRepository? = null
    val data: MutableLiveData<LoginRequest>
    val loginRequest: MutableLiveData<LoginRequest>
    val isLoading: MutableLiveData<Boolean>
    val otpSend: MutableLiveData<OtpModel>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun isLoading(): LiveData<Boolean?> {
        return isLoading
    }

    fun startLoading(isLoadingValue: Boolean) {
        isLoading.value = isLoadingValue
    }

    init {
        errorRequest = MutableLiveData()
        loginRepository = OtpRepository.instance
        data = MutableLiveData()
        loginRequest = MutableLiveData()
        isLoading = MutableLiveData()
        otpSend= MutableLiveData()

    }

    /*fun loginOnServer(logRequest: LoginRequest) {
        loginRequest.value = logRequest
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.checkForPaymentMethod(
                loginRequest.value!!,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                // is ResultWrapper.Success -> data.postValue(result.value)
            }


        }

    }*/

    fun startSmsListener(client: SmsRetrieverClient) {
        loginRepository!!.startSmsListener(client)

    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }

    fun showError() = errorRequest

    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun resendOtp(mobile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.resendSms(mobile,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> otpSend.postValue(result.value)
            }


        }



    }

    fun registerUser(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.registerLoginUser(loginRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> otpSend.postValue(result.value)
            }


        }


    }


}