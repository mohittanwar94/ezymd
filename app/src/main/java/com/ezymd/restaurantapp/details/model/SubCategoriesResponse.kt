package com.ezymd.restaurantapp.details.model

import java.io.Serializable

class Product :Serializable{
    var id = 0
    var category_id = 0
    var item: String? = null
    var description: String? = null
    var image: String? = null
    var price = 0
    var sell_price = 0
    var qnty = 0
    var stock = 0
    var rating = 0
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


