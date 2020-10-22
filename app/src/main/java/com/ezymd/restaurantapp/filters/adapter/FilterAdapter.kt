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
import com.ezymd.restaurantapp.filters.model.Filter
import com.ezymd.restaurantapp.font.CustomTypeFace
import java.util.*

class FilterAdapter(
    private val context: Context,
    private val filters: HashMap<Int, Filter>,
    private val filterValuesRV: RecyclerView
) : RecyclerView.Adapter<FilterAdapter.MyViewHolder>() {
    private var selectedPostion = 0
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        myViewHolder.container.setOnClickListener {
            filterValuesRV.adapter = FilterValuesAdapter(context, position)
            selectedPostion = position
            notifyDataSetChanged()
        }
        filterValuesRV.adapter = FilterValuesAdapter(context, selectedPostion)
        myViewHolder.container.setBackgroundColor(if (selectedPostion == position) Color.WHITE else Color.TRANSPARENT)
        myViewHolder.title.text = filters[position]!!.name
        setSelectedViews(myViewHolder, true)
    }


    private fun setSelectedViews(myViewHolder: MyViewHolder, isSelected: Boolean) {
        if (isSelected) {
            myViewHolder.title.typeface = CustomTypeFace.bold
            myViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            myViewHolder.title.typeface = CustomTypeFace.book
            myViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.gray_9e9e9e))
        }

    }

    override fun getItemCount(): Int {
        return filters.size
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