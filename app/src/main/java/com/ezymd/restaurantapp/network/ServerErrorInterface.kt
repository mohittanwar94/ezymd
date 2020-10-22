package com.ezymd.restaurantapp.network

interface ServerErrorInterface {
    fun onServerFailure(statuCode: Int, message: String?)
}