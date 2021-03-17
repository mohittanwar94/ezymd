package com.ezymd.restaurantapp.itemdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.cancel

class ItemDetailViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
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
        errorRequest = MutableLiveData()


    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

    fun addToCart(item: ItemModel) {
        val arrayListData = EzymdApplication.getInstance().cartData.value
        val arrayList: ArrayList<ItemModel>
        if (arrayListData == null)
            arrayList = ArrayList()
        else
            arrayList = arrayListData

        var isExist = false
        var i = 0
        for (itemModel in arrayList) {
            if (itemModel.id == item.id) {
                isExist = true
                itemModel.quantity = item.quantity
                arrayList[i] = itemModel
            }
            i++
        }
        if (!isExist)
            arrayList.add(item)

        EzymdApplication.getInstance().cartData.postValue(arrayList)

    }

    fun removeItem(item: ItemModel) {
        val arrayList = EzymdApplication.getInstance().cartData.value
        if (arrayList != null) {
            val index = getIndexOfItem(item.id, arrayList)
            if (index < arrayList.size)
                arrayList.removeAt(index)
            // SnapLog.print("is remove from array====" + arrayList.remove(item))
            EzymdApplication.getInstance().cartData.postValue(arrayList)
        }
    }

    private fun getIndexOfItem(id: Int, arrayList: ArrayList<ItemModel>): Int {
        var index = 0
        for (item in arrayList) {
            if (item.id == id) {
                return index
            } else {
                index++
            }

        }
        return index
    }

    fun clearCart() {
        EzymdApplication.getInstance().cartData.postValue(ArrayList())
    }


}