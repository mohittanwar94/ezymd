package com.ezymd.restaurantapp.filters.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SmoothCheckBox
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.filters.FilterViewModel
import com.ezymd.restaurantapp.filters.model.FilterInnerModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import java.util.*

class FilterValuesAdapter(
    private val context: Context,
    viewModel: FilterViewModel
) : RecyclerView.Adapter<FilterValuesAdapter.MyViewHolder>() {
    private val filters = ArrayList<FilterInnerModel>()
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.filter_value_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val filter = filters[position]
        myViewHolder.name.text = filter.filterValueName
        setSelectedViews(myViewHolder, filter.isSelected)
        myViewHolder.checkBox.isClickable = false
        myViewHolder.itemView.setOnClickListener {
            filter.isSelected = !filter.isSelected
            if (filter.isSingleSelected) {
                setOhterUnselected(myViewHolder.adapterPosition)
                notifyDataSetChanged()

            } else {
                setSelectedViews(myViewHolder, filter.isSelected)
            }
        }
    }

    private fun setOhterUnselected(adapterPosition: Int) {
        var i = 0
        for (filterModel in filters) {
            if (i != adapterPosition)
                filterModel.isSelected = false
            i++
        }

    }

    private fun setSelectedViews(myViewHolder: MyViewHolder, isSelected: Boolean) {
        if (isSelected) {
            myViewHolder.name.typeface = CustomTypeFace.medium
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.color_0a0a0a))
            myViewHolder.checkBox.isChecked = true
        } else {
            myViewHolder.name.typeface = CustomTypeFace.roman
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.color_6c6c6c))
            myViewHolder.checkBox.isChecked = false
        }

    }

    override fun getItemCount(): Int {
        return filters.size
    }

    fun setData(data: ArrayList<FilterInnerModel>) {
        filters.clear()
        filters.addAll(data)
        this.notifyDataSetChanged()
    }

    class MyViewHolder(var container: View) : RecyclerView.ViewHolder(
        container
    ) {
        var name: SnapTextView
        var checkBox: SmoothCheckBox

        init {
            checkBox = container.findViewById(R.id.checkbox)
            name = container.findViewById(R.id.name)
        }
    }
}