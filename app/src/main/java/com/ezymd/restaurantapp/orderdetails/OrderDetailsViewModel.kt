package com.ezymd.restaurantapp.orderdetails


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.refer.ReferModel
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class OrderDetailsViewModel : ViewModel() {

    var errorRequest: MutableLiveData<String>
    private var loginRepository: OrderDetailRepository? = null
    val isLoading: MutableLiveData<Boolean>
    val baseResponse: MutableLiveData<ReferModel>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = OrderDetailRepository.instance
        isLoading = MutableLiveData()
        errorRequest = MutableLiveData()
        baseResponse = MutableLiveData()

    }

    fun emailInvoice(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.emailInvoice(
                baseRequest,
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
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage!!)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

}
