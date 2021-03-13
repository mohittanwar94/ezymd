package com.ezymd.restaurantapp.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.CategoriesResponse
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
    private var categoryRepository: CategoryRepository? = null
    val mResturantData: MutableLiveData<CategoriesResponse>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {
        categoryRepository = CategoryRepository.instance
        isLoading = MutableLiveData()
        mResturantData = MutableLiveData()
        errorRequest = MutableLiveData()
        isLoading.postValue(true)

    }


    fun loadShopDetail(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryRepository!!.shopDetails(
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


    fun loadShopCategoryWithProduct(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryRepository!!.shopProductCategoryDetails(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success ->{} /*mResturantData.postValue(result.value)*/
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