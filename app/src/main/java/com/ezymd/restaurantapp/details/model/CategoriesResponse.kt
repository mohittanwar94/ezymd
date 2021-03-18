package com.ezymd.restaurantapp.details.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Category {
    @get:SerializedName("id")
    var id = 0

    @get:SerializedName("name")
    var name: String? = null


}

class Banner {
    @get:SerializedName("id")
    var id = 0

    @get:SerializedName("shop_id")
    var shop_id = 0

    @get:SerializedName("name")
    var name: String? = null

    @get:SerializedName("banner")
    var banner: String? = null

    @get:SerializedName("status")
    var status = 0

    @get:SerializedName("created_at")
    var created_at: Date? = null

    @get:SerializedName("updated_at")
    var updated_at: Date? = null
}

class CategoriesResponse {
    @get:SerializedName("data")
    var data: CategoriesAndBannersData? = null

    @get:SerializedName("message")
    var message: String? = null

    @get:SerializedName("status")
    var status = 0
}

class CategoriesAndBannersData {
    @get:SerializedName("categories")
    var categories: List<Category>? = null

    @get:SerializedName("banners")
    var banners: List<Banner>? = null
}