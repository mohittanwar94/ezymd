package com.ezymd.restaurantapp.reviewsList.model

import java.util.*

data class ShopReviewsBaseModel(var message: String?, var status: Int?, var data: RootData?)
data class RootData(
    var listing: ArrayList<ShopRating>?,
    var total: Int?,
    var excellentRating: Int?,
    var goodRating: Int?,
    var averageRating: Int?,
    var belowAverage: Int?,
    var totalReviews: Int?,
    var poor: Int?
)


data class ShopRating(
    val order_id: Int,
    val delivery_rating: Int,
    val restaurant_rating: Int,
    val feedback: String,
    val name: String,
    val image: String,
    val user_id: Int
)