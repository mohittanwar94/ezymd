package com.ezymd.restaurantapp.refer

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.ValueChangedListener
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.menu_item_row.view.*

class ReferAdapter(
    viewModel: ReferViewModel,
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<ItemModel>
) :
    RecyclerView.Adapter<ReferAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val viewModelDetails: ReferViewModel
    private val mContext: Context
    private val data = ArrayList<ItemModel>()


    init {
        this.viewModelDetails = viewModel
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

        if (!TextUtils.isEmpty(data[position].image)) {
            GlideApp.with(mContext.applicationContext)
                .load(data[position].image).centerCrop().override(200, 200).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.itemView.ivNotesThumb)
        } else {
            holder.itemView.ivNotesThumb.visibility = View.GONE
        }

        // holder.itemView.ivNotesThumb.transitionName = "thumbnailTransition";
        val item = data[position]
        item.stock = 5
        holder.itemView.dishName.text = item.item
        holder.itemView.addOn.text = item.description
        holder.itemView.addOn.setOnClickListener {
            UIUtil.clickAlpha(it)
            if (mContext is ReferActivity)
                mContext.showBottomSheet(it, data[holder.adapterPosition])

        }
        holder.itemView.price.text = mContext.getString(R.string.dollor) + item.price
        holder.itemView.quantityPicker.max = item.stock

        SnapLog.print("stock====" + item.quantity)
        holder.itemView.quantityPicker.alpha = 1f
        holder.itemView.add.alpha = 1f

        if (item.quantity == 0) {
            holder.itemView.add.visibility = View.VISIBLE
            holder.itemView.quantityPicker.visibility = View.GONE
        } else {
            holder.itemView.add.visibility = View.GONE
            holder.itemView.quantityPicker.visibility = View.VISIBLE
            holder.itemView.quantityPicker.value = item.quantity
        }
        if (item.is_veg == 0) {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_veg)
        } else {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_non_veg)
        }

        holder.itemView.add.setOnClickListener {
            if (item.stock == 0) {
                viewModelDetails.errorRequest.value =
                    mContext.getString(R.string.this_item_is_not_available)
                return@setOnClickListener
            }
            item.quantity = 1
            it.animate().alpha(0f).setDuration(250).start()
            holder.itemView.quantityPicker.alpha = 0.0f
            holder.itemView.quantityPicker.visibility = View.VISIBLE
            holder.itemView.quantityPicker.animate().alpha(1f).setDuration(250).start()

            holder.itemView.quantityPicker.postDelayed(Runnable {
                holder.itemView.add.visibility = View.GONE
                holder.itemView.quantityPicker.value = 1
                holder.itemView.quantityPicker.alpha = 1f
                data[position] = item
                //notifyItemChanged(position)
                viewModelDetails.addToCart(item)

            }, 250)
            // onRecyclerView.onClick(position, it)
        }
        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }

        addListener(holder, position)
    }

    private fun addListener(holder: NotesHolder, position: Int) {
        holder.itemView.quantityPicker.setLimitExceededListener { limit, exceededValue ->

        }


        holder.itemView.quantityPicker.valueChangedListener =
            ValueChangedListener { value, action ->
                SnapLog.print("quantity====" + value)
                val item = data[position]
                if (value < 1) {
                    holder.itemView.quantityPicker.animate().alpha(0f).setDuration(250).start()
                    item.quantity = 0
                    holder.itemView.add.alpha = 0.0f
                    holder.itemView.add.visibility = View.VISIBLE
                    holder.itemView.add.animate().alpha(1f).setDuration(250)
                        .setUpdateListener { animation ->
                            holder.itemView.add.alpha =
                                (250 / if (animation!!.currentPlayTime <= 0) 1 else animation.currentPlayTime).toFloat()
                        }.start()
                    holder.itemView.quantityPicker.value = 0
                    data[position] = item
                    viewModelDetails.removeItem(item)
                } else {

                    item.quantity = value
                    data[position] = item
                    viewModelDetails.addToCart(item)
                }

                //notifyItemChanged(position)
            }

    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}