package com.ezymd.restaurantapp.ui.home

import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.dashboard.model.TrendingDashboardModel
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SingleLiveEvent
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val isGPSEnable: MutableLiveData<Boolean>
    var errorRequest: SingleLiveEvent<String>
    private var loginRepository: HomeRepository? = null
    val address: MutableLiveData<LocationModel>
    val mPagerData: MutableLiveData<ResturantModel>
    val mTrendingData: MutableLiveData<TrendingDashboardModel>
    val mResturantData: MutableLiveData<TrendingDashboardModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = HomeRepository.instance
        address = MutableLiveData()
        isGPSEnable = MutableLiveData()
        isLoading = MutableLiveData()
        mPagerData = MutableLiveData()
        mResturantData = MutableLiveData()
        mTrendingData = MutableLiveData()
        errorRequest = SingleLiveEvent()
        isLoading.postValue(true)


    }

    fun getFilters(baseRequest: BaseRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getFilters(
                baseRequest,
                Dispatchers.IO
            )
            when (result) {
                is ResultWrapper.Success -> {
                    EzymdApplication.getInstance().filterModel.postValue(result.value.data)
                }
            }

        }

    }

    fun getAddress(locationModel: LocationModel, gcd: Geocoder) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository!!.getAddress(gcd, locationModel, address, isLoading)
        }

    }

    fun getBanners(baseRequest: BaseRequest) {
        //isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.listBanners(
                baseRequest,
                Dispatchers.IO
            )
            //  isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("viewpagerdata" + result.value)
                    mPagerData.postValue(result.value)
                }
            }

        }

    }

    fun getResturants(baseRequest: BaseRequest) {
        //  isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getResturants(
                baseRequest,
                Dispatchers.IO
            )
            // isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("restaurantdata" + result.value)
                    mResturantData.postValue(result.value)
                }
            }

        }

    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun getTrending(baseRequest: BaseRequest) {

        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getTrending(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("mTrendingData" + result.value)
                    mTrendingData.postValue(result.value)
                }
            }

        }

    }


}