package com.ezymd.restaurantapp.ui.myorder.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel

class OrderDiffUtilsCallBack(data: ArrayList<OrderModel>, neData: ArrayList<OrderModel>) :
    DiffUtil.Callback() {


    private val mOldEmployeeList: java.util.ArrayList<OrderModel> = data
    private val mNewEmployeeList: java.util.ArrayList<OrderModel> = neData
    override fun getOldListSize(): Int {
        return mOldEmployeeList.size
    }

    override fun getNewListSize(): Int {
        return mNewEmployeeList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: OrderModel = mOldEmployeeList[oldItemPosition]
        val newItem: OrderModel = mNewEmployeeList[newItemPosition]
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldLog: OrderModel = mOldEmployeeList[oldItemPosition]
        val newLog: OrderModel = mNewEmployeeList[newItemPosition]
        val isMatched =
            oldLog.paymentId.equals(newLog.paymentId, true) && oldLog.restaurant.name.equals(
                newLog.restaurant.name,
                true
            ) && oldLog.total == newLog.total
        return isMatched
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}


