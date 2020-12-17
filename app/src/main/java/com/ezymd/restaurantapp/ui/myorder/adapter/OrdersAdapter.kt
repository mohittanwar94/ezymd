package com.ezymd.restaurantapp.ui.myorder.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.TimeUtils
import kotlinx.android.synthetic.main.order_item_row.view.*

class OrdersAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<OrderModel>
) :
    RecyclerView.Adapter<OrdersAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<OrderModel>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(neData: java.util.ArrayList<OrderModel>) {
        val diffCallback = OrderDiffUtilsCallBack(data, neData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(neData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<OrderModel> {
        return data
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {


        val item = data[position]
        holder.itemView.order_id.text = "#" + item.orderId
        holder.itemView.name.text = item.restaurantName
        val spannable =
            SpannableString(holder.itemView.context.getString(R.string.order_amount) + ": ")
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    holder.itemView.totalAmount.context,
                    R.color.color_002366
                )
            ), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val spannable1 =
            SpannableString(holder.itemView.context.getString(R.string.dollor) + item.total)
        holder.itemView.totalAmount.text = TextUtils.concat(spannable, spannable1)


        holder.itemView.created.text = TimeUtils.getReadableDate(item.created)
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }
    }


    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}