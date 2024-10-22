package com.ezymd.restaurantapp.orderdetails.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.myorder.model.OrderItems
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.cart_item_row.view.dishName
import kotlinx.android.synthetic.main.cart_item_row.view.price
import kotlinx.android.synthetic.main.order_details_item_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsAdapter(
    context: Context,
    val currency: String,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<OrderItems>
) :
    RecyclerView.Adapter<OrderDetailsAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<OrderItems>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.order_details_item_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<OrderItems>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        val item = data[position]
        holder.itemView.dishName.text = item.item
        holder.itemView.price.text = currency + (item.price * item.qty)

        holder.itemView.qty.text = "" + item.qty + "x" + item.price

        if (item.product_option_name.equals("")) {
            holder.itemView.sizeVariant.visibility = View.GONE
        } else {
            holder.itemView.sizeVariant.visibility = View.VISIBLE
            holder.itemView.sizeVariant.text = item.product_option_name
        }


    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}