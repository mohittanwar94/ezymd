package com.ezymd.restaurantapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.splash.ConfigData
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String>
    private var loginRepository: LoginRepository? = null
    val loginRequest: MutableLiveData<LoginRequest>
    val loginResponse: MutableLiveData<LoginModel>
    val otpResponse: MutableLiveData<OtpModel>
    val isLoading: MutableLiveData<Boolean>
    val isShowDialog: MutableLiveData<Boolean>

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
        loginRepository = LoginRepository.instance
        loginRequest = MutableLiveData()
        isLoading = MutableLiveData()
        otpResponse = MutableLiveData()
        loginResponse = MutableLiveData()
        isShowDialog = MutableLiveData()
    }

    fun generateOtp(otp: String, counCode: String, dialCode: String) {
        val phoneUtil = PhoneNumberUtil.getInstance()
        try {
            val swissNumberProto = phoneUtil.parse(otp, counCode)
            val isValid = phoneUtil.isValidNumber(swissNumberProto) // returns true
            if (!isValid)
                errorRequest.postValue("Phone No. not valid")
            else {
                isShowDialog.postValue(true)
            }
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
        }


    }

    fun generateOtpServer(otp: String, dialCode: String) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.generateOtp(
                otp, dialCode,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> otpResponse.postValue(result.value)
            }
        }
    }

    fun loginFb(acessToken: AccessToken) {
        loginRepository!!.fbLogin(acessToken, loginRequest)

    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage!!)
    }

    fun showError() = errorRequest

    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun loginGoogle(resultGoogleSignIN: Task<GoogleSignInAccount>) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository!!.googleLogin(resultGoogleSignIN, loginRequest)

        }

    }

    fun login(it: LoginRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.loginUser(
                it,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> loginResponse.postValue(result.value)
            }
        }

    }

    fun contentVisiblity(configData: String): String {
        var configModel = Gson().fromJson(configData, ConfigData::class.java)
        return configModel.data?.otp_consent_message
    }

}


