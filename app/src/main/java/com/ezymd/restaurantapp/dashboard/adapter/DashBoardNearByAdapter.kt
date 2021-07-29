package com.ezymd.restaurantapp.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UserInfo
import kotlinx.android.synthetic.main.restaurant_item_row.view.*

class DashBoardNearByAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<DataTrending>
) :
    RecyclerView.Adapter<DashBoardNearByAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<DataTrending>()


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

    fun setData(neData: java.util.ArrayList<DataTrending>) {
        val diffCallback = DiffUtilsTrendingCallBack(data, neData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(neData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<DataTrending> {
        return data
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {

        GlideApp.with(mContext.applicationContext)
            .load(data[position].banner).centerCrop().override(200, 200).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.ivNotesThumb)

        holder.itemView.ivNotesThumb.transitionName = "thumbnailTransition";
        val item = data[position]
        holder.itemView.tvTitle.text = item.name
        holder.itemView.foodType.text = item.cuisines
        holder.itemView.discount.visibility = View.GONE
        setDiscount(item, holder.itemView.discount)
        setRatings(item, holder.itemView.rating)


        holder.itemView.tvTitle.text = item.name
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(holder.adapterPosition, it)
        }
    }

    private fun setDiscount(item: DataTrending, discountTextView: SnapTextView) {
        val discount = java.lang.StringBuilder("")
        if (item.discount.toInt() > 0) {
            discount.append(item.discount)
            discount.append(" ")
            discount.append(mContext.getString(R.string.percent_discount))
            discount.append(" | ")
            discount.append(mContext.getString(R.string.use_coupon_code))
            discountTextView.visibility = View.VISIBLE
            discountTextView.text = discount.toString()
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
            discount.append(" ")
            discount.append(UserInfo.getInstance(rating.context).currency)
            discount.append(item.min_order)
        }

        rating.text = discount.toString()


    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
        SnapLog.print("notify data set changed=========")
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}