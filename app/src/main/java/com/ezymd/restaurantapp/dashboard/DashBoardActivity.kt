package com.ezymd.restaurantapp.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardNearByAdapter
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardPagerAdapter
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardTrendingAdapter
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.filters.FilterActivity
import com.ezymd.restaurantapp.ui.search.SearchActivity
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class DashBoardActivity : BaseActivity() {
    private lateinit var registrationTutorialPagerAdapter: DashBoardPagerAdapter
    private var adapterTrending: DashBoardTrendingAdapter? = null
    private var adapterRestaurant: DashBoardNearByAdapter? = null
    private val dataTrending = ArrayList<DataTrending>()
    private val dataResturant = ArrayList<DataTrending>()
    private val dataBanner = ArrayList<DataTrending>()
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
        setBannerPager()
    }


    private fun setBannerPager() {
        bannerPager.offscreenPageLimit = 1
        bannerPager.clipToPadding = false;
        bannerPager.setPadding(20, 0, 40, 0);
        bannerPager.pageMargin = 20;

        registrationTutorialPagerAdapter =
            DashBoardPagerAdapter(
                this@DashBoardActivity,
                dataBanner, OnRecyclerView { position, view ->
                    if (typeCategory == StoreType.RESTAURANT) {
                        val intent = Intent(this@DashBoardActivity, DetailsActivity::class.java)
                        intent.putExtra(
                            JSONKeys.OBJECT,
                            getRestaurantObject(dataBanner[position])
                        )
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@DashBoardActivity, CategoryActivity::class.java)
                        intent.putExtra(JSONKeys.OBJECT, dataBanner[position])
                        startActivity(intent)
                    }
                    overridePendingTransition(R.anim.left_in, R.anim.left_out)
                    EzymdApplication.getInstance().cartData.postValue(null)

                })
        bannerPager.adapter = registrationTutorialPagerAdapter
        dots_indicator.setViewPager(bannerPager)

        setPageChangeListener()

    }

    private fun setPageChangeListener() {
        bannerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
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
                if (typeCategory == StoreType.RESTAURANT) {
                    val intent = Intent(this@DashBoardActivity, DetailsActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, getRestaurantObject(dataResturant[position]))
                    startActivity(intent)
                    overridePendingTransition(R.anim.left_in, R.anim.left_out)
                    EzymdApplication.getInstance().cartData.postValue(null)
                    return
                }
                val intent = Intent(this@DashBoardActivity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                startActivity(intent)
                overridePendingTransition(R.anim.left_in, R.anim.left_out)

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
                if (typeCategory == StoreType.RESTAURANT) {
                    val intent = Intent(this@DashBoardActivity, DetailsActivity::class.java)
                    intent.putExtra(JSONKeys.OBJECT, getRestaurantObject(dataTrending[position]))
                    startActivity(intent)
                    overridePendingTransition(R.anim.left_in, R.anim.left_out)
                    EzymdApplication.getInstance().cartData.postValue(null)
                    return
                }
                val intent = Intent(this@DashBoardActivity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataTrending[position])
                startActivity(intent)
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
                EzymdApplication.getInstance().cartData.postValue(null)

            }
        }, dataTrending)

        trendingRecyclerView.adapter = adapterTrending


    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("category_id", "" + typeCategory)
        viewModel.getTrendingStores(baseRequest)
        viewModel.nearByBanner(baseRequest)
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
        viewModel.bannerData.observe(this, Observer {

            if (it.data != null && it.data.size != 0) {
                bannerPager.visibility = View.VISIBLE
                dots_indicator.visibility = View.VISIBLE
                dataBanner.clear()
                dataBanner.addAll(it.data)
                registrationTutorialPagerAdapter.notifyDataSetChanged()
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
                    TextUtils.concat("" + dataResturant.size + " " + getAroundYouString(typeCategory))
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

    private fun getAroundYouString(typeCategory: Int): String {
        if (typeCategory == StoreType.RESTAURANT)
            return this.getString(R.string.resurant_around_you)
        else if (typeCategory == StoreType.Grocery)
            return this.getString(R.string.grocery_around_you)
        else
            return this.getString(R.string.pharmacy_around_you)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_FIRST_USER) {
            dataResturant.clear()
            adapterRestaurant?.clearData()
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap["category_id"] = "" + typeCategory
            viewModel.nearByShops(baseRequest)
            //clearAllFilter()
        } else if (requestCode == JSONKeys.FILTER && resultCode == Activity.RESULT_OK) {
            //applyAllFilter()
            dataResturant.clear()
            adapterRestaurant?.clearData()
            val baseRequest = BaseRequest(userInfo)
            baseRequest.paramsMap.putAll(data?.getSerializableExtra(JSONKeys.FILTER_MAP) as HashMap<String, String>)
            baseRequest.paramsMap["category_id"] = "" + typeCategory
            viewModel.nearByShops(baseRequest)

        }
    }

    private fun setHeaderData() {
        filter.setOnClickListener {
            UIUtil.clickAlpha(it)
            startActivityForResult(
                Intent(this@DashBoardActivity, FilterActivity::class.java),
                JSONKeys.FILTER
            )
            overridePendingTransition(R.anim.left_in, R.anim.left_out)

        }
        search.setOnClickListener {
            UIUtil.clickAlpha(it)
            val valueIntent = Intent(this@DashBoardActivity, SearchActivity::class.java)
            valueIntent.putExtra(JSONKeys.TYPE, typeCategory)
            valueIntent.putExtra(JSONKeys.OBJECT, dataResturant)
            startActivity(valueIntent)
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
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