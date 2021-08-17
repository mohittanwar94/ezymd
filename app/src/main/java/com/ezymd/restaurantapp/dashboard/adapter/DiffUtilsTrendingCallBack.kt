package com.ezymd.restaurantapp.dashboard.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ezymd.restaurantapp.dashboard.model.DataTrending

class DiffUtilsTrendingCallBack(data: ArrayList<DataTrending>, neData: ArrayList<DataTrending>) :
    DiffUtil.Callback() {


    private val mOldEmployeeList: java.util.ArrayList<DataTrending> = data
    private val mNewEmployeeList: java.util.ArrayList<DataTrending> = neData
    override fun getOldListSize(): Int {
        return mOldEmployeeList.size
    }

    override fun getNewListSize(): Int {
        return mNewEmployeeList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: DataTrending = mOldEmployeeList[oldItemPosition]
        val newItem: DataTrending = mNewEmployeeList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldLog: DataTrending = mOldEmployeeList[oldItemPosition]
        val newLog: DataTrending = mNewEmployeeList[newItemPosition]
        val isMatched = oldLog.name.equals(newLog.name, true) && oldLog.banner.equals(
            newLog.banner,
            true
        ) && oldLog.cuisines.equals(newLog.cuisines, true) && oldLog.id == newLog.id
        return isMatched
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}


