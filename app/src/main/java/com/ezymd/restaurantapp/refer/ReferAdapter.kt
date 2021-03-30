package com.ezymd.restaurantapp.refer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.OnRecyclerView

class ReferAdapter(
   val context: Context,
    val onRecyclerViewClick: OnRecyclerView,
    dataRestaurant: ArrayList<ReferModel>
) :
    RecyclerView.Adapter<ReferAdapter.NotesHolder>() {


    private val data = ArrayList<ReferModel>()


    init {
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.refer_transaction_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<ReferModel>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {


    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}