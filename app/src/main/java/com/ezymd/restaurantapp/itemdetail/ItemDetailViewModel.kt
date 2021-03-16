package com.ezymd.restaurantapp.itemdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.location.model.LocationModel
import kotlinx.coroutines.cancel

class ItemDetailViewModel : ViewModel() {

    private var itemDetailRepository: ItemDetailRepository? = null
    val address: MutableLiveData<LocationModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        itemDetailRepository = ItemDetailRepository.instance
        address = MutableLiveData()
        isLoading = MutableLiveData()


    }


}