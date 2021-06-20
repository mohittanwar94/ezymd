package com.ezymd.restaurantapp.splash

import com.google.gson.annotations.SerializedName


data class ConfigData(
    @SerializedName("data") var data: DataConfigModel,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Int

)