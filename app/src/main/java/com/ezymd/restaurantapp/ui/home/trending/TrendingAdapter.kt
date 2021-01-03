package com.ezymd.restaurantapp.ui.home.trending

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.home.model.Trending
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.SnapLog
import kotlinx.android.synthetic.main.restaurant_item_row.view.*
import kotlinx.android.synthetic.main.trending_item_row.view.*
import kotlinx.android.synthetic.main.trending_item_row.view.ivNotesThumb
import kotlinx.android.synthetic.main.trending_item_row.view.tvTitle


class TrendingAdapter(
    mContext: Context,
    onRecyclerView: OnRecyclerView,
    dataResturant: ArrayList<Trending>
) :
    RecyclerView.Adapter<TrendingAdapter.NotesHolder>() {
    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<Trending>()


    init {
        this.onRecyclerView = onRecyclerView
        this.mContext = mContext
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.trending_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val item = data[position]
        holder.itemView.tvTitle.text = item.item
        GlideApp.with(mContext.applicationContext)
            .load(data[position].image).centerCrop().override(200, 200).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.itemView.ivNotesThumb)


        SnapLog.print("trending===" + data[position].image)
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(holder.adapterPosition, it)
        }

    }

    fun setData(dataTrend: ArrayList<Trending>) {
        data.clear()
        data.addAll(dataTrend)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<Trending> {
        return data
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}