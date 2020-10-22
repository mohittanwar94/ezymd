package com.ezymd.restaurantapp.ui.home.trending

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.OnRecyclerView

class TrendingAdapter(mContext: Context, onRecyclerView: OnRecyclerView) :
    RecyclerView.Adapter<TrendingAdapter.NotesHolder>() {
    private val onRecyclerView: OnRecyclerView
    private val mContext: Context


    init {
        this.onRecyclerView = onRecyclerView
        this.mContext = mContext
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.trending_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return 3
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}