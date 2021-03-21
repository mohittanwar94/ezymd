package com.ezymd.restaurantapp.itemdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.itemdetail.ItemDetailViewModel
import com.ezymd.restaurantapp.itemdetail.model.Options
import kotlinx.android.synthetic.main.modifier_item_row.view.*

class SubOptionAdapter(
    val mContext: AppCompatActivity,
    val options: Options
) : RecyclerView.Adapter<SubOptionAdapter.NotesHolder>() {
    val data = options.data
    private val viewModel by lazy {
        ViewModelProvider(mContext).get(ItemDetailViewModel::class.java)
    }


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
        if (viewModel.selectedOptionsList.value == null)
            viewModel.selectedOptionsList.value = HashMap()
        if (viewModel.selectedOptionsList.value?.containsKey(options.title) == false) {
            viewModel.selectedOptionsList.value?.put(options.title, item)
            viewModel.selectedOptionsList.postValue(viewModel.selectedOptionsList.value)
        }
        if (viewModel.selectedOptionsList.value?.get(options.title) == item)
            holder.itemView.tv_name.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.color_ffe600)
        else
            holder.itemView.tv_name.backgroundTintList =
                ContextCompat.getColorStateList(mContext, R.color.color_f8f8f8)
        holder.itemView.tv_name.setOnClickListener {
            if (viewModel.selectedOptionsList.value?.get(options.title) == item) {
                viewModel.selectedOptionsList.value?.remove(options.title)
            } else {
                viewModel.selectedOptionsList.value?.put(options.title, item)
            }
            viewModel.selectedOptionsList.postValue(viewModel.selectedOptionsList.value)
            notifyDataSetChanged()
        }
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}