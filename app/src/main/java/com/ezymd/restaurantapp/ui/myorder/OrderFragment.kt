package com.ezymd.restaurantapp.ui.myorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.orderdetails.OrderDetailsActivity
import com.ezymd.restaurantapp.ui.myorder.adapter.OrdersAdapter
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_orders.*

class OrderFragment : Fragment() {

    private var restaurantAdapter: OrdersAdapter? = null
    private var isNullViewRoot = false
    private lateinit var searchViewModel: OrderViewModel
    private var viewRoot: View? = null

    private val dataResturant = ArrayList<OrderModel>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo
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
            searchViewModel.orderList(BaseRequest(userInfo))
        }

    }

    private fun setAdapterRestaurant() {
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
                startActivity(Intent(requireActivity(), OrderDetailsActivity::class.java).putExtra(JSONKeys.OBJECT,dataResturant[position]))
            }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter


    }

    override fun onResume() {
        super.onResume()
        setObservers()
    }

    private fun setObservers() {
        searchViewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                progress.visibility = View.GONE
            } else {
                progress.visibility = View.VISIBLE
            }
        })

        searchViewModel.baseResponse.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS && it.data != null) {
                dataResturant.clear()
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)
                }

            } else {
                (activity as BaseActivity).showError(false, it.message, null)
            }

        })

        searchViewModel.errorRequest.observe(this, androidx.lifecycle.Observer {
            (activity as BaseActivity).showError(false, it, null)
        })


    }

    override fun onStop() {
        super.onStop()
        searchViewModel.isLoading.removeObservers(this)
        searchViewModel.errorRequest.removeObservers(this)
        searchViewModel.baseResponse.removeObservers(this)
    }


}