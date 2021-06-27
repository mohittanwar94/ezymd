package com.ezymd.restaurantapp

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
import com.ezymd.restaurantapp.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    val isForceUpgrade = MutableLiveData<Boolean>(false)
    val appUpgradeVersion: MutableLiveData<Int>


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {
        appUpgradeVersion = MutableLiveData()


    }


    suspend fun contentVisiblity(configData: String) {
        var configModel = Gson().fromJson(configData, ConfigData::class.java)
        appUpgradeVersion.postValue(configModel.data.versionCode)
        isForceUpgrade.postValue(configModel.data.forceUpgrade == 1)

    }


}