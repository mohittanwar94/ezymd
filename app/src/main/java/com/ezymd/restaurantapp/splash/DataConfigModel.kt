package com.ezymd.restaurantapp.splash

import com.google.gson.annotations.SerializedName

data class DataConfigModel(

    @SerializedName("id") var id: Int,
    @SerializedName("force_upgrade") var forceUpgrade: Int,
    @SerializedName("version_code") var versionCode: Int,
    @SerializedName("restaurant") var restaurant: Int,
    @SerializedName("grocery") var grocery: Int,
    @SerializedName("pharmacy") var pharmacy: Int,
    @SerializedName("primary") var primary: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String

)
