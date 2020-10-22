package com.ezymd.restaurantapp.filters

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.filters.adapter.FilterAdapter
import kotlinx.android.synthetic.main.filter_layout.*
import kotlinx.android.synthetic.main.header_new.*

class FilterActivity : AppCompatActivity() {
    var filterAdapter: FilterAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_layout)
        initControls()
    }

    private fun initControls() {
        filterRV.setLayoutManager(LinearLayoutManager(this))
        filterRV.addItemDecoration(DividerItemDecoration(this, R.drawable.item_dacorator_grey))
        filterValuesRV.setLayoutManager(LinearLayoutManager(this))
        done.visibility=View.VISIBLE
        done.text=getString(R.string.clear_filters)

        headertext.visibility=View.VISIBLE
        headertext.text=getString(R.string.filters)

        /*List<String> colors = Arrays.asList(new String[]{"Red", "Green", "Blue", "White"});
        if (!Preferences.filters.containsKey(Filter.INDEX_COLOR)) {
            Preferences.filters.put(Filter.INDEX_COLOR, new Filter("Color", colors, new ArrayList()));
        }
        List<String> sizes = Arrays.asList(new String[]{"10", "12", "14", "16", "18", "20"});
        if (!Preferences.filters.containsKey(Filter.INDEX_SIZE)) {
            Preferences.filters.put(Filter.INDEX_SIZE, new Filter("Size", sizes, new ArrayList()));
        }
        List<String> prices = Arrays.asList(new String[]{"0-100", "101-200", "201-300"});
        if (!Preferences.filters.containsKey(Filter.INDEX_PRICE)) {
            Preferences.filters.put(Filter.INDEX_PRICE, new Filter("Price", prices, new ArrayList()));
        }

        filterAdapter = new FilterAdapter(getApplicationContext(), Preferences.filters, filterValuesRV);
        filterRV.setAdapter(filterAdapter);
*/
    }
}