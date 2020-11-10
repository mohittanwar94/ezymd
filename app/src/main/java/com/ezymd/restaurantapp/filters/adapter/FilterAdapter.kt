package com.ezymd.restaurantapp.filters.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.filters.FilterViewModel
import com.ezymd.restaurantapp.filters.model.Filter
import com.ezymd.restaurantapp.filters.model.FilterInnerModel

class FilterAdapter(
    private val context: Context,
    private val filters: ArrayList<Filter>,
    private val filterValuesRV: RecyclerView
) : RecyclerView.Adapter<FilterAdapter.MyViewHolder>() {
    private var selectedPosition:Int = 0
    private val adapterFilter by lazy {
        FilterValuesAdapter(context)
    }

    init {
        filterValuesRV.adapter = adapterFilter
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val data = filters[position]
        myViewHolder.itemView.setOnClickListener {
            selectedPosition = myViewHolder.adapterPosition
            adapterFilter.setData(filters[selectedPosition].data)
            notifyDataSetChanged()
        }
        if (selectedPosition == position) {
            adapterFilter.setData(filters[selectedPosition].data)
        }
        myViewHolder.container.setBackgroundColor(if (selectedPosition == position) Color.parseColor("#fafbfc") else Color.TRANSPARENT)
        myViewHolder.title.text = data.filterName
        setSelectedViews(myViewHolder, (selectedPosition == position))
    }

    private fun setAdapterRecyclerView(data: ArrayList<FilterInnerModel>) {
        adapterFilter.setData(data)
    }


    private fun setSelectedViews(myViewHolder: MyViewHolder, isSelected: Boolean) {
        if (isSelected) {
            myViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.color_ffb912))
        } else {
            myViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.color_6c6c6c))
        }

    }

    override fun getItemCount(): Int {
        return filters.size
    }

    fun setData(list: ArrayList<Filter>) {
        filters.clear()
        filters.addAll(list)
        notifyDataSetChanged()

    }

    class MyViewHolder(var container: View) : RecyclerView.ViewHolder(
        container
    ) {
        var title: TextView

        init {
            title = container.findViewById(R.id.title)
        }
    }
}



