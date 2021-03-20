package com.ezymd.restaurantapp.itemdetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.itemdetail.ItemDetailViewModel
import com.ezymd.restaurantapp.itemdetail.ProductDetailActivity
import com.ezymd.restaurantapp.itemdetail.model.Options
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.option_item_row.view.*

class OptionsAdapter(
    val mContext: AppCompatActivity,
    dataRestaurant: ArrayList<Options>
) : RecyclerView.Adapter<OptionsAdapter.NotesHolder>() {

    private val data = ArrayList<Options>()

    init {
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.option_item_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<Options>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        val item = data[position]

        holder.itemView.tv_title.text = item.title
        val registrationTutorialPagerAdapter = SubOptionAdapter(
            mContext,
            item)
        holder.itemView.rv_modifiers.adapter = registrationTutorialPagerAdapter

    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}