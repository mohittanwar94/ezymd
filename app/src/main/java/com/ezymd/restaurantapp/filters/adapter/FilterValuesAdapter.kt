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
import com.ezymd.restaurantapp.filters.model.FilterInnerModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import java.util.*

class FilterValuesAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val filters = ArrayList<FilterInnerModel>()
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        when (getItemViewType(position)) {
            0 -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.filter_value_item, viewGroup, false)
                return MyViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.filter_value_item, viewGroup, false)
                return MyViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.filter_value_price, viewGroup, false)
                return RatingViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.filter_value_item, viewGroup, false)
                return MyViewHolder(view)
            }


        }

    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {
        when (parent.itemViewType) {
            0 -> {
                val myViewHolder = parent as MyViewHolder
                setFilterData(myViewHolder, position)
            }
            1 -> {
                val myViewHolder = parent as MyViewHolder
                setFilterData(myViewHolder, position)
            }
            2 -> {
                val myViewHolder = parent as RatingViewHolder
                setRating(myViewHolder, position)
            }
            else -> {
                val myViewHolder = parent as MyViewHolder
                setFilterData(myViewHolder, position)
            }
        }

    }

    private fun setRating(myViewHolder: FilterValuesAdapter.RatingViewHolder, position: Int) {


    }

    private fun setFilterData(myViewHolder: MyViewHolder, position: Int) {
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

    override fun getItemViewType(position: Int): Int {
         return 0
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
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.color_1e222a))
            myViewHolder.checkBox.isChecked = true
        } else {
            myViewHolder.name.typeface = CustomTypeFace.roman
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.color_787a7f))
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

    class RatingViewHolder(var container: View) : RecyclerView.ViewHolder(container)


}