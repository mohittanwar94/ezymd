package com.ezymd.restaurantapp.refer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.TimeUtils
import kotlinx.android.synthetic.main.refer_transaction_row.view.*

class ReferAdapter(
    val context: Context,
    dataRestaurant: ArrayList<Transaction>
) :
    RecyclerView.Adapter<ReferAdapter.NotesHolder>() {


    private val data = ArrayList<Transaction>()


    init {
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.refer_transaction_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<Transaction>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val item = data[position]
        holder.itemView.tv_name.text = item.description
        if (item.transaction_type == 2) {
            //credit
            holder.itemView.price.text = "+ " +holder.itemView.context.getString(R.string.dollor)+ item.amount
            holder.itemView.price.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.color_48ce5f
                )
            )
        } else {
            //debit
            holder.itemView.price.text = "- "  +holder.itemView.context.getString(R.string.dollor)+ item.amount
            holder.itemView.price.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.pay
                )
            )
        }
        holder.itemView.tv_name.text = item.description
        holder.itemView.date_time.text=TimeUtils.getReadableDate(item.created_at)

    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}