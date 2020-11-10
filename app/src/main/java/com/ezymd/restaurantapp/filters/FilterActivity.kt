package com.ezymd.restaurantapp.filters

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.filters.adapter.FilterAdapter
import com.ezymd.restaurantapp.filters.model.*
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ShowDialog
import com.ezymd.restaurantapp.utils.UIUtil
import kotlinx.android.synthetic.main.filter_layout.*
import kotlinx.android.synthetic.main.header_new.*

class FilterActivity : BaseActivity() {
    var filterAdapter: FilterAdapter? = null
    private val viewModel by lazy {
        ViewModelProvider(this).get(FilterViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_layout)
        initControls()
        setGUI()
    }

    private fun setGUI() {
        leftIcon.setOnClickListener {
            UIUtil.clickAlpha(it)
            onBackPressed()
        }
        done.setOnClickListener {
            clearFilter(it)
        }
        apply.setOnClickListener {
            UIUtil.clickHandled(it)
            setResult(Activity.RESULT_OK)
            this.finish()
        }

    }

    private fun clearFilter(it: View?) {
        UIUtil.clickAlpha(it)
        val dataModel = DataModel()
        val filter = ArrayList<Filter>()
        val sorting = Sorting()
        var i = 0
        for (sort in list) {
            if (i == 0) {
                sorting.id = sort.filterId
                sorting.name = sort.filterName
                sorting.data = ArrayList()
                for (filterModel in sort.data) {
                    val sortModel = Sort()
                    sortModel.id = filterModel.filterValueId
                    sortModel.name = filterModel.filterValueName
                    sortModel.isSelected = false
                    sorting.data.add(sortModel)
                }
            } else {
                for (filterModel in sort.data) {
                    filterModel.isSelected = false
                }
                filter.add(sort)

            }
            i++
        }
        dataModel.filters = filter
        dataModel.sorting = sorting
        EzymdApplication.getInstance().filterModel.postValue(dataModel)
        setResult(Activity.RESULT_FIRST_USER)
        this.finish()

    }


    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {

            if (it.data != null) {
                EzymdApplication.getInstance().filterModel.postValue(it.data)
            }
        })
        EzymdApplication.getInstance().filterModel.observe(this, Observer {
            if (it != null) {
                processData(it)
            } else {
                viewModel.getFilters(BaseRequest(userInfo))
            }
        })

        viewModel.errorRequest.observe(this, Observer {
            ShowDialog(this).disPlayDialog(it, false, false)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    val list = ArrayList<Filter>()
    private fun processData(dataModel: DataModel) {
        list.clear()
        val filteNewModel = Filter()
        filteNewModel.filterId = dataModel.sorting.id
        filteNewModel.filterName = dataModel.sorting.name
        for (sort in dataModel.sorting.data) {
            val filterInner = FilterInnerModel()
            filterInner.filterValueId = sort.id
            filterInner.filterValueName = sort.name
            filterInner.isSelected = sort.isSelected
            filterInner.sort_by = sort.sort_by
            filterInner.sort_order = sort.sort_order
            filterInner.isSingleSelected = true
            filteNewModel.data.add(filterInner)
        }
        list.add(filteNewModel)
        list.addAll(dataModel.filters)

        filterAdapter?.setData(list)
        viewModel.isLoading.postValue(false)
    }


    override fun onStop() {
        super.onStop()
        viewModel.errorRequest.removeObservers(this)
        viewModel.isLoading.removeObservers(this)
        viewModel.mResturantData.removeObservers(this)
        EzymdApplication.getInstance().filterModel.removeObservers(this)


    }

    override fun onDestroy() {
        super.onDestroy()
        getUpdateData()
    }

    private fun getUpdateData() {
        val dataModel = DataModel()
        val filter = ArrayList<Filter>()
        val sorting = Sorting()
        var i = 0
        for (sort in list) {
            if (i == 0) {
                sorting.id = sort.filterId
                sorting.name = sort.filterName
                sorting.data = ArrayList()
                for (filterModel in sort.data) {
                    val sortModel = Sort()
                    sortModel.id = filterModel.filterValueId
                    sortModel.name = filterModel.filterValueName
                    sortModel.sort_by = filterModel.sort_by
                    sortModel.sort_order = filterModel.sort_order
                    sortModel.isSelected = filterModel.isSelected
                    sorting.data.add(sortModel)
                }
            } else {
                filter.add(sort)

            }
            i++
        }
        dataModel.filters = filter
        dataModel.sorting = sorting
        EzymdApplication.getInstance().filterModel.postValue(dataModel)
    }

    private fun initControls() {
        filterRV.layoutManager = LinearLayoutManager(this)
        filterRV.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        filterValuesRV.layoutManager = LinearLayoutManager(this)
        done.visibility = View.VISIBLE
        done.text = getString(R.string.clear_filters)
        done.setTextColor(ContextCompat.getColor(this,R.color.color_ffb912))
        headertext.visibility = View.VISIBLE
        headertext.text = getString(R.string.filters)

        val list = ArrayList<Filter>()
        filterAdapter = FilterAdapter(applicationContext, list, filterValuesRV, viewModel)
        filterRV.adapter = filterAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}