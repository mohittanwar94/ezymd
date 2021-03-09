package com.ezymd.restaurantapp.dashboard.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import kotlinx.android.synthetic.main.dash_trending_item_row.view.*

class DashBoardTrendingAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataRestaurant: ArrayList<DataTrending>
) :
    RecyclerView.Adapter<DashBoardTrendingAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView = onRecyclerViewClick
    private val mContext: Context = context
    private val data = ArrayList<DataTrending>()


    init {
        data.addAll(dataRestaurant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dash_trending_item_row, parent, false)
        )
    }

    fun getData(): ArrayList<DataTrending> {
        return data
    }
    fun setData(itemList: ArrayList<DataTrending>) {
        val diffCallback = DiffUtilsTrendingCallBack(data, itemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(itemList)
        diffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        if (!TextUtils.isEmpty(data[position].banner)) {
            GlideApp.with(mContext.applicationContext)
                .load(data[position].banner).centerCrop().dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).override(950,550).placeholder(R.drawable.ic_restaurant_location)
                .into(holder.itemView.ivNotesThumb)
        }

        // holder.itemView.ivNotesThumb.transitionName = "thumbnailTransition";
        val item = data[position]
        holder.itemView.shopName.text = item.name

        holder.itemView.shopCategory.text = item.cuisines
        //setDiscount(item, holder.itemView.shopRating)
        setRatings(item, holder.itemView.shopRating)

        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }

    }


    private fun setRatings(item: DataTrending, rating: SnapTextView) {
        val discount = StringBuilder("")
        if (item.rating > 0) {
            discount.append(item.rating)
            discount.append(" ")
            discount.append(mContext.getString(R.string.rating))

        }
        if (item.is_free_delivery.toInt() != 0) {
               discount.append(" | ")
               discount.append(mContext.getString(R.string.free_delivery))
           }
        if (!item.min_order.equals("0")) {
            discount.append(" | ")
            discount.append(mContext.getString(R.string.minimum))
            discount.append(" $")
            discount.append(item.min_order)
        }

        rating.text = discount.toString()


    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}