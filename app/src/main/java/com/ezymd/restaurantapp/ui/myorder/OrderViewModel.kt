package com.ezymd.restaurantapp.ui.myorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Order"
    }
    val text: LiveData<String> = _text
}