package com.ezymd.restaurantapp.reviewsList.model

import java.util.*

data class ShopReviewsBaseModel(var message: String?, var status: Int?, var data: RootData?)
data class RootData(
    var listing: ArrayList<ShopRating>?,
    var total: Double?,
    var excellentRating: Double?,
    var goodRating: Double?,
    var averageRating: Double?,
    var belowAverage: Double?,
    var totalReviews: Int?,
    var poor: Double?
)


data class ShopRating(
    val order_id: Int,
    val delivery_rating: Double,
    val restaurant_rating: Double,
    val feedback: String,
    val name: String,
    val image: String,
    val user_id: Int
)