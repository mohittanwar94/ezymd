package com.ezymd.restaurantapp.filters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.ezymd.restaurantapp.utils.*
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
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
        ratingSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SnapLog.print("leftValue" + ratingSeekBar.leftSeekBar.progress.toString())
                data.rating = ratingSeekBar.leftSeekBar.progress.toString()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {


            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                textView.text = TextUtils.concat("Rating: ", getRating(data.rating))

            }
        })

        priceSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                SnapLog.print("leftValue" + priceSeekBar.leftSeekBar.progress.toString())
                SnapLog.print("leftValue" + priceSeekBar.rightSeekBar.progress.toString())
                data.min_price = leftValue.toString()
                data.max_price = rightValue.toString()

                textView.text =
                    "Price: " + getString(R.string.dollor) + leftValue.toInt().toString() +
                            " - " + getString(R.string.dollor) + rightValue.toInt().toString()

            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })
        leftIcon.setOnClickListener {
            UIUtil.clickAlpha(it)
            onBackPressed()
        }
        done.setOnClickListener {
            clearFilter(it)
        }
        apply.setOnClickListener {
            UIUtil.clickHandled(it)
            setResult(Activity.RESULT_OK, Intent().putExtra(JSONKeys.FILTER_MAP, getSortedData()))
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
    var data = DataModel()
    private fun processData(dataModel: DataModel) {
        data = dataModel
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

        if (!checkhaveRatingAndPrice(dataModel.filters)) {
            val rating = Filter()
            rating.filterId = 1000
            rating.filterName = "Rating"
            rating.viewType = 2

            val priceFilter = Filter()
            priceFilter.filterId = 1001
            priceFilter.filterName = "Cost per person"
            priceFilter.viewType = 2
            list.add(rating)
            list.add(priceFilter)

        }

        filterAdapter?.setData(list)
        viewModel.isLoading.postValue(false)
    }

    private fun checkhaveRatingAndPrice(filters: ArrayList<Filter>): Boolean {
        for (filter in filters) {
            if (filter.filterId == 1000 || filter.filterId == 1001)
                return true
        }

        return false
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
        val dataModel = data
        val filter = ArrayList<Filter>()
        val sorting = Sorting()
        for ((i, sort) in list.withIndex()) {
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
        }
        dataModel.filters = filter
        dataModel.sorting = sorting
        EzymdApplication.getInstance().filterModel.postValue(dataModel)
    }

    private fun getSortedData(): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["min_price"] = data.min_price.toFloat().toInt().toString()
        hashMap["max_price"] = data.max_price.toFloat().toInt().toString()
        hashMap["min_rating"] = getRatingValue(data.rating).toString()
        hashMap["max_rating"] = "5.0"
        for ((i, sort) in list.withIndex()) {
            if (i == 0) {
                for (filterModel in sort.data) {
                    if (filterModel.isSelected) {
                        hashMap["sort_by"] = filterModel.sort_by
                        hashMap["sort_order"] = filterModel.sort_order
                    }
                }
            } else {
                if (sort.data != null) {
                    val builder = StringBuilder()
                    for (filterModel in sort.data) {
                        if (filterModel.isSelected) {
                            builder.append("," + filterModel.filterValueId)

                        }
                    }
                    if (builder.length > 1) {
                        val ids = builder.toString().substring(1)
                        hashMap["cuisines"] = ids
                        SnapLog.print(ids)
                    }
                }

            }
        }

        return hashMap
    }

    private fun initControls() {
        filterRV.layoutManager = LinearLayoutManager(this)
        filterRV.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        filterValuesRV.layoutManager = LinearLayoutManager(this)
        done.visibility = View.VISIBLE
        done.text = getString(R.string.clear_filters)
        done.background = ContextCompat.getDrawable(this, R.drawable.ic_gray_btn_pressed)
        done.setTextColor(ContextCompat.getColor(this, R.color.blue_002366))
        headertext.visibility = View.VISIBLE
        headertext.text = getString(R.string.filters)

        val list = ArrayList<Filter>()
        filterAdapter = FilterAdapter(applicationContext, list, filterValuesRV) { pos, view ->

            if (pos == 1045) {
                ratingSeekBar.visibility = View.GONE
                priceSeekBar.visibility = View.GONE
                textView.visibility = View.GONE
                return@FilterAdapter
            }
            textView.visibility = View.VISIBLE
            if (list[pos].viewType == 2 && list[pos].filterName.equals("Rating")) {
                ratingSeekBar.visibility = View.VISIBLE
                priceSeekBar.visibility = View.GONE
                ratingSeekBar.setProgress(data.rating.toFloat())
                textView.text = TextUtils.concat("Rating: ", getRating(data.rating))
            } else {
                priceSeekBar.visibility = View.VISIBLE
                ratingSeekBar.visibility = View.GONE
                priceSeekBar.setProgress(data.min_price.toFloat(), data.max_price.toFloat())
            }
        }
        filterRV.adapter = filterAdapter
    }

    private fun getRating(rating: String): String {

        if (rating.toDouble() == 1.25) {
            return "3.0 +"
        } else if (rating.toDouble() == 2.5) {
            return "3.5 +"
        } else if (rating.toDouble() == 3.75) {
            return "4.0 +"
        } else if (rating.toDouble() == 5.0) {
            return "4.5 +"
        } else {
            return "Any"
        }

    }

    private fun getRatingValue(rating: String): Double {

        if (rating.toDouble() == 1.25) {
            return 3.0
        } else if (rating.toDouble() == 2.5) {
            return 3.5
        } else if (rating.toDouble() == 3.75) {
            return 4.0
        } else if (rating.toDouble() == 5.0) {
            return 4.5
        } else {
            return 0.0
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}