package com.ezymd.restaurantapp.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.cart.model.TransactionChargeModel
import com.ezymd.restaurantapp.coupon.model.CoupanModel
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    val coupanModel = MutableLiveData<CoupanModel>()
    var errorRequest: SingleLiveEvent<String>
    private var loginRepository: CartRepository? = null
    val mTransactionCharge: MutableLiveData<TransactionChargeModel>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {

        loginRepository = CartRepository.instance
        isLoading = MutableLiveData()
        mTransactionCharge = MutableLiveData()
        errorRequest = SingleLiveEvent()


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


    fun getCharges(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository!!.getTransactionCharges(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mTransactionCharge.postValue(result.value)
            }
        }


    }

    private fun showNetworkError() {
        errorRequest.postValue(EzymdApplication.getInstance().networkErrorMessage)
    }


    private fun showGenericError(error: ErrorResponse?) {
        errorRequest.postValue(error?.message)
    }

}