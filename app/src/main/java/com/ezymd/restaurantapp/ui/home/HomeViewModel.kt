package com.ezymd.restaurantapp.ui.home

import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.LocationValidatorModel
import com.ezymd.restaurantapp.dashboard.model.TrendingDashboardModel
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.splash.ConfigData
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SingleLiveEvent
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var noBanner = false
    var noTrending = false
    var noStores = false
    private lateinit var trendingJob: Job
    private lateinit var restaurantJob: Job
    private lateinit var bannerJob: Job
    val isGPSEnable: MutableLiveData<Boolean>
    var errorRequest: SingleLiveEvent<String>
    private var loginRepository: HomeRepository? = null
    val address: MutableLiveData<LocationModel>
    val mPagerData: MutableLiveData<TrendingDashboardModel>
    val mConfigData: MutableLiveData<JsonObject>
    val mTrendingData: MutableLiveData<TrendingDashboardModel>
    val mResturantData: MutableLiveData<TrendingDashboardModel>
    val mReferralResponse: MutableLiveData<LocationValidatorModel>
    val isLoading: MutableLiveData<Boolean>
    val isPharmacyVisible: MutableLiveData<Boolean>
    val isRestuantVisible: MutableLiveData<Boolean>
    val isGroceryVisible: MutableLiveData<Boolean>
    val primaryCategory = MutableLiveData<Int>(1)
    val isAllHide = MutableLiveData<Boolean>(false)


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = HomeRepository.instance
        address = MutableLiveData()
        isGPSEnable = MutableLiveData()
        isLoading = MutableLiveData()
        isPharmacyVisible = MutableLiveData()
        isGroceryVisible = MutableLiveData()
        isRestuantVisible = MutableLiveData()
        mPagerData = MutableLiveData()
        mResturantData = MutableLiveData()
        mConfigData = MutableLiveData()
        mTrendingData = MutableLiveData()
        errorRequest = SingleLiveEvent()
        isLoading.postValue(true)
        mReferralResponse = MutableLiveData()


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


    fun getConfigurations() {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getConfigurations(
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("viewpagerdata" + result.value)
                    mConfigData.postValue(result.value)
                }
            }

        }

    }

    fun getBanners(baseRequest: BaseRequest) {
        baseRequest.paramsMap["category_id"] = "" + primaryCategory.value
        //isLoading.postValue(true)
        if (this::bannerJob.isInitialized)
            bannerJob.cancel()
        bannerJob = viewModelScope.launch(Dispatchers.IO) {
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
        baseRequest.paramsMap["category_id"] = "" + primaryCategory.value
        if (this::restaurantJob.isInitialized)
            restaurantJob.cancel()
        restaurantJob = viewModelScope.launch(Dispatchers.IO) {
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
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage!!)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun getTrending(baseRequest: BaseRequest) {
        baseRequest.paramsMap["category_id"] = "" + primaryCategory.value
        isLoading.postValue(true)
        if (this::trendingJob.isInitialized)
            trendingJob.cancel()
        trendingJob = viewModelScope.launch(Dispatchers.IO) {
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

    fun saveReferral(baseRequest: BaseRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.saveReferralOnServer(
                baseRequest,
                Dispatchers.IO
            )
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("mTrendingData" + result.value)
                    mReferralResponse.postValue(result.value)
                }
            }

        }

    }

    suspend fun contentVisiblity(configData: String) {
        var configModel = Gson().fromJson(configData, ConfigData::class.java)
        isGroceryVisible.postValue(configModel.data.grocery == 1)
        isPharmacyVisible.postValue(configModel.data.pharmacy == 1)
        isRestuantVisible.postValue(configModel.data.restaurant == 1)
        if (configModel.data.grocery != 1 && configModel.data.pharmacy != 1 && configModel.data.restaurant != 1)
            isAllHide.postValue(true)
        primaryCategory.postValue(configModel.data.primary)
    }


}