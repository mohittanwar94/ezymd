package com.ezymd.restaurantapp.details.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.utils.GlideApp
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.menu_item_row.view.*

class ProductAdapter(
    private val context: Context,
    private val data: ArrayList<Product>,
    private val onRecyclerView: OnRecyclerView
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.menu_item_row, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        if (!TextUtils.isEmpty(data[position].image)) {
            GlideApp.with(context.applicationContext)
                .load(data[position].image).centerCrop().override(200, 200).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.itemView.ivNotesThumb)
        } else {
            holder.itemView.ivNotesThumb.visibility = View.GONE
        }

        // holder.itemView.ivNotesThumb.transitionName = "thumbnailTransition";
       // item.stock = 5
        holder.itemView.dishName.text = item.item
        holder.itemView.addOn.text = item.description
        holder.itemView.addOn.setOnClickListener {
            UIUtil.clickAlpha(it)
            if (context is CategoryActivity)
                context.showBottomSheet(it, data[holder.adapterPosition])
        }
        holder.itemView.price.text = context.getString(R.string.dollor) + item.price
        holder.itemView.quantityPicker.max = item.stock

        SnapLog.print("stock====" + item.qnty)
        holder.itemView.quantityPicker.alpha = 1f
        holder.itemView.add.alpha = 1f

        if (item.qnty == 0) {
            holder.itemView.add.visibility = View.VISIBLE
            holder.itemView.quantityPicker.visibility = View.GONE
        } else {
            holder.itemView.add.visibility = View.GONE
            holder.itemView.quantityPicker.visibility = View.VISIBLE
            holder.itemView.quantityPicker.value = item.qnty
        }
        holder.itemView.vegLabel.visibility=View.GONE
     /*   if (item.is_veg == 0) {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_veg)
        } else {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_non_veg)
        }*/


    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(list: ArrayList<Product>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()

    }

    class MyViewHolder(var container: View) : RecyclerView.ViewHolder(container)
}
