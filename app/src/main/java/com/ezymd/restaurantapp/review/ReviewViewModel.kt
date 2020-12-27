package com.ezymd.restaurantapp.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class ReviewViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String>
    private var loginRepository: ReviewRepository? = null
    val loginResponse: MutableLiveData<LocationValidatorModel>
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
        loginRepository = ReviewRepository.instance
        isLoading = MutableLiveData()
        loginResponse = MutableLiveData()
    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }

    fun showError() = errorRequest

    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }


    fun saveRating(it: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.saveRating(
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

}


