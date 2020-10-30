package com.ezymd.restaurantapp.ui.home.trending

import androidx.recyclerview.widget.DiffUtil
import com.ezymd.restaurantapp.ui.home.model.Resturant

class DiffUtilsCallBack(data: ArrayList<Resturant>, neData: ArrayList<Resturant>) :
    DiffUtil.Callback() {


    private val mOldEmployeeList: java.util.ArrayList<Resturant> = data
    private val mNewEmployeeList: java.util.ArrayList<Resturant> = neData
    override fun getOldListSize(): Int {
        return mOldEmployeeList.size
    }

    override fun getNewListSize(): Int {
        return mNewEmployeeList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: Resturant = mOldEmployeeList[oldItemPosition]
        val newItem: Resturant = mNewEmployeeList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldLog: Resturant = mOldEmployeeList[oldItemPosition]
        val newLog: Resturant = mNewEmployeeList[newItemPosition]
        val isMatched = oldLog.name.equals(newLog.name, true) && oldLog.banner.equals(
            newLog.banner,
            true
        ) && oldLog.category.equals(
            newLog.category,
            true
        ) && oldLog.cuisines.equals(newLog.cuisines, true) && oldLog.distance == newLog.distance
        return isMatched
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}


