package com.ezymd.restaurantapp.coupon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.coupon.CouponActivity
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.coupon_item_row.view.*

class CouponAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataRestaurant: ArrayList<ItemModel>
) : RecyclerView.Adapter<CouponAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<ItemModel>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.coupon_item_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<ItemModel>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        holder.itemView.couponCode.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }

        holder.itemView.tnc.setOnClickListener {
            (mContext as CouponActivity).showBottomSheet(it, data[position])
        }

    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}