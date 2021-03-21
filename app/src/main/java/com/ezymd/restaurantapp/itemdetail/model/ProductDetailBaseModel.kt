package com.ezymd.restaurantapp.itemdetail.model

import com.ezymd.restaurantapp.details.model.Product
import java.util.*

data class ProductDetailBaseModel(var message: String?, var status: Int?, var data: RootData?)
data class RootData(
    var product: ArrayList<Product>?,
    var images: ArrayList<ImageModel>?,
    var options: ArrayList<Options>?
)

data class Options(var title: String, var data: ArrayList<Modifier>)
data class Modifier(var id: Int, var title: String, var price: Double, var operator: String)
data class ImageModel(val id: Int, val image: String, val sort_order: Int)