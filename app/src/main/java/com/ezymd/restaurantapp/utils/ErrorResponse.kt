package com.ezymd.restaurantapp.utils

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ErrorResponse(
    @SerializedName("error_code") @Expose var error_code: Int,
    @SerializedName("status") @Expose var status: String,
    @SerializedName("message") @Expose var message: String = ""
) : Parcelable
