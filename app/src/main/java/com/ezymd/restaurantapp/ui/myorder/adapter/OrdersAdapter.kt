package com.ezymd.restaurantapp.ui.myorder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.CartActivity
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.tracker.TrackerActivity
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.ui.myorder.model.OrderStatus
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.order_item_row.view.*

class OrdersAdapter(
    context: Context,
    onRecyclerViewClick: OnRecyclerView,
    dataResturant: ArrayList<OrderModel>
) :
    RecyclerView.Adapter<OrdersAdapter.NotesHolder>() {


    private val onRecyclerView: OnRecyclerView
    private val mContext: Context
    private val data = ArrayList<OrderModel>()


    init {
        this.onRecyclerView = onRecyclerViewClick
        this.mContext = context
        data.addAll(dataResturant)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_item_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(neData: java.util.ArrayList<OrderModel>) {
        val diffCallback = OrderDiffUtilsCallBack(data, neData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data.clear()
        data.addAll(neData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<OrderModel> {
        return data
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotesHolder, position: Int) {


        val item = data[position]
        holder.itemView.order_id.text =
            holder.itemView.order_id.context.getString(R.string.orderID) + " #" + item.orderId
        holder.itemView.name.text = item.restaurantName

        holder.itemView.totalAmount.text =
            holder.itemView.context.getString(R.string.dollor) + item.total

        val itemsString = StringBuilder()
        for (model in item.orderItems) {
            itemsString.append(model.item)
            itemsString.append(" x ")
            itemsString.append(model.qty)
            itemsString.append("\n")
        }

        holder.itemView.items.text = itemsString.toString()
        holder.itemView.created.text = TimeUtils.getReadableDate(item.created)
        holder.itemView.trackOrder.setOnClickListener {
            val startIntent = Intent(holder.itemView.context, TrackerActivity::class.java)
            startIntent.putExtra(JSONKeys.OBJECT, data[position])
            (mContext as MainActivity).startActivity(startIntent)
            SnapLog.print("status==="+data[position].orderStatus)
        }

        holder.itemView.reorder.setOnClickListener {
            UIUtil.clickHandled(it)
            makeReorder(data[position])
        }
        if (item.orderStatus == OrderStatus.ORDER_COMPLETED) {
            holder.itemView.trackOrder.visibility = View.GONE
            holder.itemView.reorder.visibility = View.VISIBLE
        } else {
            holder.itemView.trackOrder.visibility = View.VISIBLE
            holder.itemView.reorder.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onRecyclerView.onClick(position, it)
        }
    }

    private fun makeReorder(orderModel: OrderModel) {
        val list = ArrayList<ItemModel>()
        for (item in orderModel.orderItems) {
            val model = ItemModel()

            model.id = item.id
            model.quantity = item.qty
            model.price = item.price

            list.add(model)
        }
        EzymdApplication.getInstance().cartData.postValue(list)

        val restaurnt = Resturant()
        restaurnt.id = orderModel.restaurantID
        restaurnt.name = orderModel.restaurantName
        restaurnt.address = orderModel.address
        restaurnt.lat = orderModel.restaurant_lat.toDouble()
        restaurnt.longitude = orderModel.restaurant_lang.toDouble()
        restaurnt.address = orderModel.address

        val intent = Intent(mContext, CartActivity::class.java)
        intent.putExtra(JSONKeys.OBJECT, restaurnt)
        mContext.startActivity(intent)
        (mContext as BaseActivity).overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }


    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }


    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}