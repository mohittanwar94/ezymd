package com.ezymd.restaurantapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.splash.ConfigData
import com.ezymd.restaurantapp.utils.UserInfo
import com.google.gson.Gson
import kotlinx.coroutines.cancel

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


    suspend fun contentVisiblity(userInfo: UserInfo) {
        val configModel = Gson().fromJson(userInfo.configJson, ConfigData::class.java)
        appUpgradeVersion.postValue(configModel.data.versionCode)
        isForceUpgrade.postValue(configModel.data.forceUpgrade == 1)
        userInfo.currency = configModel?.data?.currentZone?.currency_symbol
        userInfo.currencyCode = configModel?.data?.currentZone?.currency_code
    }


}