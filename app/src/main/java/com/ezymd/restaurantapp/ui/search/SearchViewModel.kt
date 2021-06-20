package com.ezymd.restaurantapp.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.dashboard.model.TrendingDashboardModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.splash.ConfigData
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SingleLiveEvent
import com.ezymd.restaurantapp.utils.StoreType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchViewModel() : ViewModel() {
    private lateinit var  searchJob: Job
     val primaryCategory = MutableLiveData<Int>(StoreType.RESTAURANT)
    val isGPSEnable: MutableLiveData<Boolean>
    var errorRequest: SingleLiveEvent<String>
    private var loginRepository: SearchRepository? = null
    val mResturantData: MutableLiveData<TrendingDashboardModel>
    val mSearchData: MutableLiveData<TrendingDashboardModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = SearchRepository.instance
        isLoading = MutableLiveData()
        mSearchData = MutableLiveData()
        mResturantData = MutableLiveData()
        isGPSEnable = MutableLiveData()
        errorRequest = SingleLiveEvent()


    }


    fun getResturants(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        baseRequest.paramsMap.put("category_id", "" + primaryCategory.value)
        if (this::searchJob.isInitialized)
            searchJob.cancel()
        searchJob=viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getRestaurants(
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


    fun searchRestaurants(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getRestaurantSearch(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mSearchData.postValue(result.value)
            }

        }

    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    suspend fun contentVisiblity(configData: String) {
        var configModel = Gson().fromJson(configData, ConfigData::class.java)
        primaryCategory.postValue(configModel.data.primary)
    }


}