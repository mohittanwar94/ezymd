package com.ezymd.restaurantapp.cart

import android.content.Context
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
import kotlinx.android.synthetic.main.cart_item_row.view.*
import kotlinx.android.synthetic.main.restaurant_item_row.view.ivNotesThumb

class CartAdapter(
    viewModel: CartViewModel,
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<ItemModel>
) :
    RecyclerView.Adapter<CartAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val viewModelDetails: CartViewModel
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
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_row, parent, false)
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
        item.stock = 5
        holder.itemView.price.text = mContext.getString(R.string.dollor) + item.total
        holder.itemView.quantityPicker.max = item.stock

        SnapLog.print("stock====" + item.quantity)
        holder.itemView.quantityPicker.alpha = 1f

        holder.itemView.quantityPicker.value = item.quantity


        var productVariantids = ""
        for (modifier in item.listModifiers) {
            productVariantids = productVariantids + ", " + modifier.title
        }
        if (productVariantids.length > 1)
            productVariantids = productVariantids.substring(1, productVariantids.length)
        if (productVariantids.length > 1) {
            holder.itemView.modifiers.visibility = View.VISIBLE
            holder.itemView.modifiers.text = productVariantids
        }else{
            holder.itemView.modifiers.visibility = View.GONE
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
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    viewModelDetails.removeItem(item)
                } else {

                    item.quantity = value
                    data[position] = item
                    viewModelDetails.addToCart(item)
                }

            }

    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}