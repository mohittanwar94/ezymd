package com.ezymd.restaurantapp.itemdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.itemdetail.model.ImageModel
import com.ezymd.restaurantapp.itemdetail.model.Modifier
import com.ezymd.restaurantapp.itemdetail.model.Options
import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ItemDetailViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
    private var itemDetailRepository: ItemDetailRepository? = null
    val address: MutableLiveData<LocationModel>
    val isLoading: MutableLiveData<Boolean>
    var product = MutableLiveData<ArrayList<Product>>()
    var images = MutableLiveData<ArrayList<ImageModel>>()
    var options = MutableLiveData<ArrayList<Options>>()
    val selectedOptionsList = MutableLiveData<HashMap<String, Modifier>>()

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

    fun generateUUID(): String {
        val uuid: UUID = UUID.randomUUID()
        return uuid.toString()

    }

    fun addToCart(item: ItemModel, list: ArrayList<Modifier>) {
        val arrayListData = EzymdApplication.getInstance().cartData.value
        val arrayList: ArrayList<ItemModel>
        item.uuid = generateUUID()
        item.listModifiers.addAll(list)
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
            if (itemModel.uuid == item.uuid) {
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

    fun getProductDetails(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = itemDetailRepository!!.getProductDetailsData(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> {
                    SnapLog.print("mTrendingData" + result.value)
                    product.postValue(result.value.data?.product)
                    images.postValue(result.value.data?.images)
                    options.postValue(result.value.data?.options)
                }
            }

        }
    }

    fun removeItem(id: String) {
        val arrayList = EzymdApplication.getInstance().cartData.value
        if (arrayList != null) {
            val item = arrayList.filter {
                it.uuid.equals(it)
            }
            arrayList.remove(item)
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