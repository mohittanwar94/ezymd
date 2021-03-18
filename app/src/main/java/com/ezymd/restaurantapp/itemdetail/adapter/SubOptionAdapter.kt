package com.ezymd.restaurantapp.itemdetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.itemdetail.model.Modifier
import kotlinx.android.synthetic.main.modifier_item_row.view.*

class SubOptionAdapter(
    val context: Context,
    val data: ArrayList<Modifier>
) : RecyclerView.Adapter<SubOptionAdapter.NotesHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.modifier_item_row, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        val item = data[position]

        holder.itemView.tv_name.text = item.title


    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}