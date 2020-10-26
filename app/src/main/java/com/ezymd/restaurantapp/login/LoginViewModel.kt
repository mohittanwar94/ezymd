package com.ezymd.restaurantapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String>
    private var loginRepository: LoginRepository? = null
    val loginRequest: MutableLiveData<LoginRequest>
    val isLoading: MutableLiveData<Boolean>

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


    }

    fun generateOtp(otp: String) {
        if (otp.length < 5)
            errorRequest.postValue("Phone No. not valid")
        else {
            viewModelScope.launch(Dispatchers.IO) {
                val result = loginRepository!!.generateOtp(
                    otp,
                    Dispatchers.IO
                )
                isLoading.postValue(false)
                when (result) {
                    is ResultWrapper.NetworkError -> showNetworkError()
                    is ResultWrapper.GenericError -> showGenericError(result.error)
                    // is ResultWrapper.Success -> data.postValue(result.value)
                }
            }

        }

    }

    fun loginFb(acessToken: AccessToken) {
        loginRepository!!.fbLogin(acessToken, loginRequest)

    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
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


}