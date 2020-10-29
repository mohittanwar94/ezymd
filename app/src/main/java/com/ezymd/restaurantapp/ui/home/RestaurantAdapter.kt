package com.ezymd.restaurantapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.ui.home.trending.DiffUtilsCallBack
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.restaurant_item_row.view.*

class RestaurantAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<Resturant>
) :
    RecyclerView.Adapter<RestaurantAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<Resturant>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(neData: java.util.ArrayList<Resturant>) {
        val diffCallback = DiffUtilsCallBack(data, neData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(neData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<Resturant> {
        return data
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        GlideApp.with(mContext.applicationContext)
            .load(data[position].banner).centerCrop().override(200, 200).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.ivNotesThumb)

        val item = data[position]
        holder.itemView.tvTitle.text = item.name
        holder.itemView.foodType.text = item.category
        holder.itemView.discount.visibility = View.GONE
        setDiscount(item, holder.itemView.discount)
        setRatings(item, holder.itemView.rating)


        holder.itemView.tvTitle.text = item.name
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }
    }

    private fun setDiscount(item: Resturant, discountTextView: SnapTextView) {
        val discount = java.lang.StringBuilder("")
        if (item.discount > 0) {
            discount.append(item.discount)
            discount.append(" ")
            discount.append(mContext.getString(R.string.percent_discount))
            discount.append(" | ")
            discount.append(mContext.getString(R.string.use_coupon_code))
            discountTextView.visibility = View.VISIBLE
            discountTextView.text = discount.toString()
        }

    }

    private fun setRatings(item: Resturant, rating: SnapTextView) {
        val discount = StringBuilder("")
        if (item.rating > 0) {
            discount.append(item.rating)
            discount.append(" ")
            discount.append(mContext.getString(R.string.rating))

        }
        if (item.isFreeDelivery != 0) {
            discount.append(" | ")
            discount.append(mContext.getString(R.string.free_delivery))
        }
        if (!item.minOrder.equals("0")) {
            discount.append(" | ")
            discount.append(mContext.getString(R.string.minimum))
            discount.append(" ")
            discount.append(item.minOrder)
        }

        rating.text = discount.toString()


    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}