package com.ezymd.restaurantapp.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.menu_item_row.view.*
import kotlinx.android.synthetic.main.restaurant_item_row.view.ivNotesThumb

class MenuAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<ItemModel>
) :
    RecyclerView.Adapter<MenuAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<ItemModel>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.menu_item_row, parent, false)
        )
    }

    fun setData(itemList: ArrayList<ItemModel>) {
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        GlideApp.with(mContext.applicationContext)
            .load(data[position].image).centerCrop().override(200, 200).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.ivNotesThumb)

        // holder.itemView.ivNotesThumb.transitionName = "thumbnailTransition";
        val item = data[position]
        holder.itemView.dishName.text = item.item
        holder.itemView.addOn.text = item.description

        holder.itemView.price.text = "" + item.price + mContext.getString(
            R.string.dollor
        )
        // setDiscount(item, holder.itemView.discount)
        // setRatings(item, holder.itemView.rating)


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