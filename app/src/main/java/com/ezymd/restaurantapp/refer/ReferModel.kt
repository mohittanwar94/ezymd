package com.ezymd.restaurantapp.refer

data class ReferModel(var message: String?, var status: Int?, var data: ReferData?)
data class ReferData(var code: String, var history: ArrayList<Transaction>, var total: String)
data class Transaction(var code: String)