package com.ezymd.restaurantapp.cart


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.cancel

class OrderConfirmViewModel : ViewModel() {

    var errorRequest: MutableLiveData<String>
    val dateSelected: MutableLiveData<String>
    val isNowSelectd: MutableLiveData<Boolean>

    private var loginRepository: CartRepository? = null
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = CartRepository.instance
        isLoading = MutableLiveData()
        errorRequest = MutableLiveData()
        isNowSelectd= MutableLiveData()
        dateSelected=MutableLiveData()


    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

}
