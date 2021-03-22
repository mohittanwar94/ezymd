package com.ezymd.restaurantapp.editprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.model.OtpModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
    private var loginRepository: EditProfileRepository? = null
    val mResturantData: MutableLiveData<LoginModel>
    val isLoading: MutableLiveData<Boolean>
    val otpResponse: MutableLiveData<OtpModel>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        otpResponse = MutableLiveData()
        loginRepository = EditProfileRepository.instance
        isLoading = MutableLiveData()
        mResturantData = MutableLiveData()
        errorRequest = MutableLiveData()


    }


    fun updateProfileInfo(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.updateUprofile(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mResturantData.postValue(result.value)
            }

        }

    }


    fun generateOtp(otp: String) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.generateOtp(
                otp,
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

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun saveImage(file: File, profileRequest: BaseRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.updateUprofile(
                file, profileRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mResturantData.postValue(result.value)

            }
        }

    }


}