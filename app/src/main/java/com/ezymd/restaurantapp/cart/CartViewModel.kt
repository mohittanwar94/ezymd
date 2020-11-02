package com.ezymd.restaurantapp.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.ui.home.model.ResturantModel
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.cancel

class CartViewModel : ViewModel() {

    var errorRequest: MutableLiveData<String>
    private var loginRepository: CartRepository? = null
    val mResturantData: MutableLiveData<ResturantModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = CartRepository.instance
        isLoading = MutableLiveData()
        mResturantData = MutableLiveData()
        errorRequest = MutableLiveData()


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
            arrayList.remove(item)
            EzymdApplication.getInstance().cartData.postValue(arrayList)
        }
    }


    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

}