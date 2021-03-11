package com.ezymd.restaurantapp.dashboard

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardNearByAdapter
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardTrendingAdapter
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashBoardActivity : BaseActivity() {
    private var adapterTrending: DashBoardTrendingAdapter? = null
    private var adapterRestaurant: DashBoardNearByAdapter? = null
    private val dataTrending = ArrayList<DataTrending>()
    private val dataResturant = ArrayList<DataTrending>()
    private val viewModel by lazy {
        ViewModelProvider(this).get(DashBoardViewModel::class.java)
    }
    private val typeCategory by lazy {
        intent.getIntExtra(JSONKeys.TYPE, 1)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setToolBar()
        setHeaderData()
        setTrendingAdapter()
        setShopAdapter()
        setObserver()
        getData()
    }

    private fun setShopAdapter() {
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._13sdp
                ))
            )
        )
        adapterRestaurant = DashBoardNearByAdapter(this@DashBoardActivity, object : OnRecyclerView {

            override fun onClick(position: Int, view: View?) {
                val intent = Intent(this@DashBoardActivity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                startActivity(intent)

            }
        }, dataResturant)

        resturantRecyclerView.adapter = adapterRestaurant


    }


    private fun setTrendingAdapter() {
        trendingRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trendingRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._13sdp
                ))
            )
        )
        adapterTrending = DashBoardTrendingAdapter(this@DashBoardActivity, object : OnRecyclerView {

            override fun onClick(position: Int, view: View?) {
                val intent = Intent(this@DashBoardActivity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataTrending[position])
                startActivity(intent)
            }
        }, dataTrending)

        trendingRecyclerView.adapter = adapterTrending


    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("category_id", "" + typeCategory)
        viewModel.getTrendingStores(baseRequest)
        viewModel.nearByShops(baseRequest)
    }

    override fun onResume() {
        super.onResume()


    }

    private fun setObserver() {
        viewModel.mTrendingData.observe(this, Observer {

            if (it.data != null && it.data.size != 0) {
                trending.visibility = View.VISIBLE
                adapterTrending?.setData(it.data)
                adapterTrending?.getData()?.let { it1 ->
                    dataTrending.addAll(it1)

                }
            } else if (it.status != ErrorCodes.SUCCESS) {
                showError(false, it.message, null)
            }
        })


        viewModel.mShopData.observe(this, Observer {

            if (it.data != null && it.data.size != 0) {
                adapterRestaurant?.setData(it.data)
                adapterRestaurant?.getData()?.let { it1 ->
                    dataResturant.addAll(it1)

                }
                if (it.data.size > 0)
                    filter.visibility = View.VISIBLE
                else
                    filter.visibility = View.GONE
                resturantCount.text =
                    TextUtils.concat("" + dataResturant.size + " " + this.getString(R.string.resurant_around_you))
            } else if (it.status != ErrorCodes.SUCCESS) {
                showError(false, it.message, null)
            }
        })

        viewModel.errorRequest.observe(this, Observer {
            if (it != null)
                showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }


    private fun setHeaderData() {
        search.setOnClickListener {
            UIUtil.clickAlpha(it)
        }

    }


    private fun setToolBar() {
        back.setOnClickListener {
            UIUtil.clickAlpha(it)
            onBackPressed()
        }

        setHeader()


    }

    private fun setHeader() {
        if (StoreType.Grocery == typeCategory) {
            trending.text = getString(R.string.trending_grocery)
            nearBy.text = getString(R.string.near_by_grocery)
        } else if (StoreType.Pharmacy == typeCategory) {
            nearBy.text = getString(R.string.near_by_pharmacy)
            trending.text = getString(R.string.trending_pharmacy)
        } else {
            trending.text = getString(R.string.trending_restaurnts)
            nearBy.text = getString(R.string.near_by_restuarnts)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}