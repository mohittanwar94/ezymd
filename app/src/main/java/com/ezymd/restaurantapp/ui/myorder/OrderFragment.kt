package com.ezymd.restaurantapp.ui.myorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.orderdetails.OrderDetailsActivity
import com.ezymd.restaurantapp.ui.myorder.adapter.OrdersAdapter
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_orders.*

class OrderFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var restaurantAdapter: OrdersAdapter? = null
    private var isNullViewRoot = false
    private lateinit var searchViewModel: OrderViewModel
    private var viewRoot: View? = null

    private val dataResturant = ArrayList<OrderModel>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo
    }

    override fun onRefresh() {
        swipeLayout.isRefreshing = true
        dataResturant.clear()
        restaurantAdapter?.clearData()
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["customer_id"] = "" + userInfo!!.userID
        searchViewModel.orderList(baseRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        isNullViewRoot = false
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_orders, container, false)
            isNullViewRoot = true
        }
        return viewRoot
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SnapLog.print("onActivityCreated")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNullViewRoot) {
            setAdapterRestaurant()
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap["customer_id"] = "" + userInfo!!.userID
            searchViewModel.orderList(baseRequest)
        }

    }

    private fun setAdapterRestaurant() {
        swipeLayout.setOnRefreshListener(this)
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                UIUtil.convertDpToPx(
                    activity,
                    requireActivity().resources.getDimension(R.dimen._3sdp)
                )
                    .toInt()
            )
        )
        restaurantAdapter =
            OrdersAdapter(activity as MainActivity, OnRecyclerView { position, view ->
                startActivity(
                    Intent(requireActivity(), OrderDetailsActivity::class.java).putExtra(
                        JSONKeys.OBJECT,
                        dataResturant[position]
                    )
                )
                requireActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter

        setObservers()

    }

    override fun onResume() {
        super.onResume()
    }

    private fun setObservers() {

        EzymdApplication.getInstance().isRefresh.observe(requireActivity(), Observer {
            if (it) {
                SnapLog.print("refresh called==================")
                dataResturant.clear()
                restaurantAdapter?.clearData()
                val baseRequest = BaseRequest(userInfo)
                baseRequest.paramsMap["customer_id"] = "" + userInfo!!.userID
                searchViewModel.orderList(baseRequest)
            }
        })

        searchViewModel.isLoading.observe(requireActivity(), androidx.lifecycle.Observer {
            if (swipeLayout == null)
                return@Observer
            if (!it) {
                swipeLayout.isRefreshing = false
                progress.visibility = View.GONE
            } else {
                progress.visibility = View.VISIBLE
            }
        })

        searchViewModel.baseResponse.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS && it.data != null) {
                dataResturant.clear()
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                }


                showEmpty(dataResturant.size)
            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })

        searchViewModel.errorRequest.observe(this, androidx.lifecycle.Observer {
            if (it != null)
                (activity as BaseActivity).showError(false, it, null)
        })


    }

    fun showEmpty(size: Int) {
        if (emptyView != null)
            emptyView.visibility = if (size == 0) View.VISIBLE else View.GONE
    }

    override fun onStop() {
        super.onStop()
    }


}