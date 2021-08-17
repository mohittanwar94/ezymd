package com.ezymd.restaurantapp.filters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.filters.model.DataModel
import com.ezymd.restaurantapp.filters.model.FilterModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FilterViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
    private var loginRepository: FilterRepository? = null
    val mResturantData: MutableLiveData<FilterModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = FilterRepository.instance
        isLoading = MutableLiveData()
        mResturantData = MutableLiveData()
        errorRequest = MutableLiveData()
        isLoading.postValue(true)


    }


    fun getFilters(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getFilters(
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

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage!!)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun updateFilterData(datModel: DataModel) {
        EzymdApplication.getInstance().filterModel.postValue(datModel)

    }


}