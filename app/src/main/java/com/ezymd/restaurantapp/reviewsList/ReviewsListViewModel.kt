package com.ezymd.restaurantapp.reviewsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.reviewsList.model.ShopRating
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*


class ReviewsListViewModel : ViewModel() {
    private var errorRequest: MutableLiveData<String> = MutableLiveData()
    private var loginRepository: ReviewsListRepository? = null
    val loginResponse: MutableLiveData<LocationValidatorModel>
    val isLoading: MutableLiveData<Boolean>
    var listing = MutableLiveData<ArrayList<ShopRating>>()
    var total = MutableLiveData<Int>()
    var excellentRating = MutableLiveData<Int>()
    var goodRating = MutableLiveData<Int>()
    var averageRating = MutableLiveData<Int>()
    var totalReviews = MutableLiveData<Int>()
    var belowAverage = MutableLiveData<Int>()
    var poor = MutableLiveData<Int>()
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
        loginRepository = ReviewsListRepository.instance
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


    fun getShopReview(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository?.getShopReview(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("mData" + result.value)
                    result.value.data?.let {
                        total.postValue(it.total)
                        excellentRating.postValue(it.excellentRating)
                        goodRating.postValue(it.goodRating)
                        averageRating.postValue(it.averageRating)
                        belowAverage.postValue(it.belowAverage)
                        totalReviews.postValue(it.totalReviews)
                        poor.postValue(it.poor)
                        listing.postValue(it.listing)
                    }
                }
            }
        }
    }
    fun getShopReviewByRating(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository?.getShopReviewByRating(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("mData" + result.value)
                    result.value.data?.let {
                        total.postValue(it.total)
                        excellentRating.postValue(it.excellentRating)
                        goodRating.postValue(it.goodRating)
                        averageRating.postValue(it.averageRating)
                        belowAverage.postValue(it.belowAverage)
                        poor.postValue(it.poor)
                        listing.postValue(it.listing)
                    }
                }
            }
        }
    }


}


