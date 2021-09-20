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
    @SerializedName("updated_at") var updatedAt: String,
    @SerializedName("otp_consent_message") var otp_consent_message: String,
    @SerializedName("current_zone") var currentZone: CurrentZone,
    @SerializedName("account_id") var accountID: String,
    @SerializedName("public_key") var publicKey: String


)


data class CurrentZone(
    @SerializedName("country_code") var country_code: String,
    @SerializedName("currency_symbol") var currency_symbol: String,
    @SerializedName("currency_code") val currency_code: String,
)