package com.ezymd.restaurantapp.reviewsList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.reviewsList.model.ShopRating
import kotlinx.android.synthetic.main.reviews_row.view.*

class RatingsAdapter(
    val mContext: AppCompatActivity,
    dataRestaurant: ArrayList<ShopRating>) : RecyclerView.Adapter<RatingsAdapter.NotesHolder>() {

    private val data = ArrayList<ShopRating>()

    init {
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reviews_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<ShopRating>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        val item = data[position]

        holder.itemView.username.text = item.name
        holder.itemView.tv_review.text = item.feedback
        holder.itemView.rating.rating = item.restaurant_rating.toFloat()
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}