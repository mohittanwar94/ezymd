package com.ezymd.restaurantapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.OnRecyclerView

class RestaurantAdapter(context: Context, onRecyclerViewClick: OnRecyclerView) :
    RecyclerView.Adapter<RestaurantAdapter.NotesHolder>() {
    private val onRecyclerView = onRecyclerViewClick
    private val mContext = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return 10
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}