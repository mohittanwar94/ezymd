package com.ezymd.restaurantapp.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.coupon.model.CoupanBaseModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class CouponViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String>
    private var loginRepository: CouponRepository? = null
    val loginResponse: MutableLiveData<CoupanBaseModel>
    val applyCoupon= MutableLiveData<LocationValidatorModel>()
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
        loginRepository = CouponRepository.instance
        isLoading = MutableLiveData()
        loginResponse = MutableLiveData()
    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage!!)
    }

    fun showError() = errorRequest

    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }


    fun listCoupon(it: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.listCoupon(
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


    fun applyCoupon(it: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.applyCoupon(
                it,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> applyCoupon.postValue(result.value!!)
            }
        }

    }

}


