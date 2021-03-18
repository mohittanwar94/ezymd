package com.ezymd.restaurantapp.itemdetail.model

import com.ezymd.restaurantapp.details.model.Product
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ProductDetailBaseModel {
    @SerializedName("product")
    @Expose
    var product = ArrayList<Product>()

    @SerializedName("images")
    @Expose
    var images = ArrayList<ImageModel>()

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null
}