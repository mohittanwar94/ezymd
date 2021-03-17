package com.ezymd.restaurantapp.details.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.details.CategoryViewModel
import com.ezymd.restaurantapp.details.model.Header
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.itemdetail.ProductDetailActivity
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.ezymd.restaurantapp.utils.OnRecyclerViewClickType
import com.ezymd.restaurantapp.utils.VerticalSpacesItemDecoration
import kotlinx.android.synthetic.main.count_parent_item.view.*

class SubCategoryWithProductAdapter(
    private val context: Context,
    private val data: ArrayList<Header>,
    private val viewModelDetails: CategoryViewModel,
    private val onRecyclerView: OnRecyclerViewClickType
) : RecyclerView.Adapter<SubCategoryWithProductAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.count_parent_item, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, parentPostion: Int) {
        val item = data[parentPostion]
        myViewHolder.itemView.title_text.text = item.name
        myViewHolder.itemView.arrow_image.rotation = if (item.isExpanded) 90f else 0.0f
        myViewHolder.itemView.setOnClickListener {
            item.isExpanded = !item.isExpanded
            myViewHolder.itemView.recycler.visibility =
                if (item.isExpanded) View.VISIBLE else View.GONE
            myViewHolder.itemView.arrow_image.rotation = if (item.isExpanded) 90f else 0.0f
            val products = item.products!! as ArrayList<Product>
            val adapterProduct = ProductAdapter(context,
                products, viewModelDetails,
                OnRecyclerView { position, view ->
                    onRecyclerView.onClick(parentPostion, position, view)
                    val intent = Intent(context, ProductDetailActivity::class.java)
                    intent.putExtra(
                        JSONKeys.OBJECT,
                        products[position]
                    )
                    context.startActivity(intent)
                })
            myViewHolder.itemView.recycler.layoutManager =
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            myViewHolder.itemView.recycler.addItemDecoration(
                VerticalSpacesItemDecoration(
                    (context.resources.getDimensionPixelSize(
                        R.dimen._13sdp
                    ))
                )
            )
            myViewHolder.itemView.recycler.adapter = adapterProduct


        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(list: ArrayList<Header>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()

    }

    class MyViewHolder(var container: View) : RecyclerView.ViewHolder(container)
}
