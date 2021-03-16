package com.ezymd.restaurantapp.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.details.model.CategoriesResponse
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.SubCategoriesResponse
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    var errorRequest: MutableLiveData<String>
    private var categoryRepository: CategoryRepository? = null
    val mResturantData: MutableLiveData<CategoriesResponse>
    val mCategoryWithProduct: MutableLiveData<CategoriesResponse>
    val mSubCategoriesResponse: MutableLiveData<SubCategoriesResponse>
    val isLoading: MutableLiveData<Boolean>

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }


    init {
        categoryRepository = CategoryRepository.instance
        isLoading = MutableLiveData()
        mResturantData = MutableLiveData()
        mCategoryWithProduct = MutableLiveData()
        mSubCategoriesResponse = MutableLiveData()
        errorRequest = MutableLiveData()
        isLoading.postValue(true)

    }


    fun loadShopDetail(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryRepository!!.shopDetails(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mResturantData.postValue(result.value)
            }

        }

    }


    fun loadShopCategoryWithProduct(baseRequest: BaseRequest) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryRepository!!.shopProductCategoryDetails(
                baseRequest,
                Dispatchers.IO
            )
            isLoading.postValue(false)
            when (result) {
                is ResultWrapper.NetworkError -> showNetworkError()
                is ResultWrapper.GenericError -> showGenericError(result.error)
                is ResultWrapper.Success -> mSubCategoriesResponse.postValue(result.value)
            }

        }

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
            arrayList.remove(item)
            EzymdApplication.getInstance().cartData.postValue(arrayList)
        }
    }

    fun clearCart() {
         EzymdApplication.getInstance().cartData.postValue(ArrayList())
    }


}