package com.ezymd.restaurantapp.details.model

import java.io.Serializable

data class Product(
    var id: Int = 0,
    var category_id: Int = 0,
    var item: String? = null,
    var description: String? = null,
    var manufactured_by: String? = null,
    var image: ArrayList<String>? = null,
    val sell_price: Int = 0,
    var price: Double = 0.0,
    var qnty: Int = 0,
    var veg_nonveg: Int = 0,
    var is_option: Int = 0,
    var stock: Int = 0,
    var rating: Int = 0,
) : Serializable {
    var calculatedPrice: Double = this.price
        get() {
            return if (sell_price == null || sell_price == 0) price else sell_price.toDouble()
        }
}

class Child {
    var name: String? = null
    var id = 0
    var products: List<Product>? = null
    var childs: List<Any>? = null
}

class Header {
    var name: String? = null
    var id = 0
    var isExpanded = false
    var products: List<Product>? = null
    var childs: List<Child>? = null
}

class SubCategoriesResponse {
    var data: List<Header>? = null
    var message: String? = null
    var status = 0
}


