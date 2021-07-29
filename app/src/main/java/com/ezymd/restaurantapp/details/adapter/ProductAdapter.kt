package com.ezymd.restaurantapp.details.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.ValueChangedListener
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.details.CategoryViewModel
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.menu_item_row.view.*

class ProductAdapter(
    private val context: Context,
    private val data: ArrayList<Product>,
    private val viewModelDetails: CategoryViewModel,
    private val onRecyclerView: OnRecyclerView
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.menu_item_row, viewGroup, false)
        return MyViewHolder(view)
    }

    fun getData(): ArrayList<Product> {
        return data
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]

        holder.itemView.add.background =
            ContextCompat.getDrawable(context, R.drawable.add_button_bg)
        holder.itemView.add.text = context.getString(R.string.add)
        holder.itemView.add.setTextColor(ContextCompat.getColor(context, R.color.color_002366))
        val productImage = data[position].image?.firstOrNull()
        SnapLog.print("product image===" + data[position].image?.firstOrNull())
        if (!TextUtils.isEmpty(productImage)) {
            GlideApp.with(context.applicationContext)
                .load(productImage).centerCrop().override(200, 200).dontAnimate()
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
        holder.itemView.price.text = UserInfo.getInstance(context).currency + item.price
        holder.itemView.quantityPicker.max = item.stock
        holder.itemView.quantityPicker.min = 0

        SnapLog.print("stock====" + item.stock)
        holder.itemView.quantityPicker.alpha = 1f
        holder.itemView.add.alpha = 1f
        holder.itemView.add.visibility = View.VISIBLE
        holder.itemView.quantityPicker.value = item.qnty
        SnapLog.print("item.qnty==========" + item.qnty)
        if (item.qnty == 0) {
            holder.itemView.add.visibility = View.VISIBLE
            holder.itemView.quantityPicker.visibility = View.GONE
        } else {
            holder.itemView.add.visibility = View.GONE
            holder.itemView.quantityPicker.visibility = View.VISIBLE
            //  holder.itemView.quantityPicker.increment(item.qnty)
        }
        holder.itemView.vegLabel.visibility = View.GONE
        if (item.veg_nonveg == 0) {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_veg)
        } else {
            holder.itemView.vegLabel.setImageResource(R.drawable.ic_non_veg)
        }
        if (item.is_option == 0) {
            holder.itemView.frameLay.visibility = View.VISIBLE
        } else {
            holder.itemView.frameLay.visibility = View.GONE
        }
        holder.itemView.add.setOnClickListener {
            if (item.stock == 0) {
                viewModelDetails.errorRequest.value =
                    context.getString(R.string.this_item_is_not_available)
                return@setOnClickListener
            }
            it.animate().alpha(0f).setDuration(250).start()
            holder.itemView.quantityPicker.alpha = 0.0f
            holder.itemView.quantityPicker.visibility = View.VISIBLE
            holder.itemView.quantityPicker.animate().alpha(1f).setDuration(250).start()

            holder.itemView.quantityPicker.postDelayed(Runnable {
                holder.itemView.add.visibility = View.GONE
                holder.itemView.quantityPicker.increment(1)
                item.qnty = 1
                holder.itemView.quantityPicker.alpha = 1f
                data[position] = item
                //notifyItemChanged(position)
                viewModelDetails.addToCart((context as BaseActivity).getItemModelObject(item))

            }, 50)
            // onRecyclerView.onClick(position, it)
        }
        holder.itemView.setOnClickListener {
            if (item.is_option != 0)
                onRecyclerView.onClick(position, it)
        }

        if (item.stock == 0) {
            holder.itemView.add.background = null
            holder.itemView.add.text = context.getString(R.string.sold_out)
            holder.itemView.add.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        addListener(holder, position)
    }

    private fun addListener(holder: ProductAdapter.MyViewHolder, position: Int) {
        holder.itemView.quantityPicker.setLimitExceededListener { limit, exceededValue ->

        }


        holder.itemView.quantityPicker.valueChangedListener =
            ValueChangedListener { value, action ->
                SnapLog.print("quantity====" + value)
                val item = data[position]
                if (value < 1) {
                    holder.itemView.quantityPicker.animate().alpha(0f).setDuration(250).start()
                    item.qnty = 0
                    holder.itemView.add.alpha = 0.0f
                    holder.itemView.add.visibility = View.VISIBLE
                    holder.itemView.add.animate().alpha(1f).setDuration(250)
                        .setUpdateListener { animation ->
                            holder.itemView.add.alpha =
                                (250 / if (animation!!.currentPlayTime <= 0) 1 else animation.currentPlayTime).toFloat()
                        }.start()
                    holder.itemView.quantityPicker.value = 0
                    data[position] = item
                    viewModelDetails.removeItem((context as BaseActivity).getItemModelObject(item))
                } else {

                    item.qnty = value
                    data[position] = item
                    viewModelDetails.addToCart((context as BaseActivity).getItemModelObject(item))
                }

                //notifyItemChanged(position)
            }

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
