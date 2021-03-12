package com.ezymd.restaurantapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.dashboard.adapter.DashBoardNearByAdapter
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.details.CategoryActivity
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchActivity : BaseActivity() {

    //private val dataResturant = ArrayList<DataTrending>()
    private var restaurantAdapter: DashBoardNearByAdapter? = null
    private val viewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private val dataResturant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as ArrayList<DataTrending>
    }


    private val typeCategory by lazy {
        intent.getIntExtra(JSONKeys.TYPE, 1)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setData()
        setAdapter()
        setSearchListerner()
        setObserver()
        search.requestFocus()
    }

    private fun setData() {
        if (StoreType.Grocery == typeCategory) {
            search.hint = getString(R.string.search_grocery_stores)
        } else if (StoreType.Pharmacy == typeCategory) {
            search.hint = getString(R.string.search_pharmacy)
        } else {
            search.hint = getString(R.string.search_resturant)
        }
    }

    private fun setSearchListerner() {
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (search.text.toString().trim().length > 4) {
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("category_id",""+typeCategory)
                    baseRequest.paramsMap.put("search", search.text.toString())
                    viewModel.searchRestaurants(baseRequest)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (search.text.toString().trim().isEmpty()) {
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("category_id",""+typeCategory)
                    viewModel.searchRestaurants(baseRequest)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }


    private fun setAdapter() {
        resturantRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resturantRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._13sdp
                ))
            )
        )
        restaurantAdapter = DashBoardNearByAdapter(this, OnRecyclerView { position, view ->
            if (typeCategory==StoreType.RESTAURANT){
                val intent = Intent(this@SearchActivity, DetailsActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, getRestaurantObject(dataResturant[position]))
                startActivity(intent)
                overridePendingTransition(R.anim.left_in,R.anim.left_out)
                EzymdApplication.getInstance().cartData.postValue(null)

            }else {
                val intent = Intent(this@SearchActivity, CategoryActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                startActivity(intent)
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }


        }, dataResturant)
        resturantRecyclerView.adapter = restaurantAdapter
        restaurantAdapter?.setData(dataResturant)
        viewModel.isLoading.postValue(false)


    }


    override fun onResume() {
        super.onResume()


    }

    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {

            if (it.data != null && it.data.size > 0) {
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 -> dataResturant.addAll(it1) }
            }
        })

        viewModel.mSearchData.observe(this, Observer {

            if (it.data != null && it.data.size > 0) {
                restaurantAdapter?.clearData()
                restaurantAdapter?.setData(it.data)
                restaurantAdapter?.getData()?.let { it1 -> dataResturant.addAll(it1) }
            }
        })

        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }


    override fun onStop() {
        super.onStop()

    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}