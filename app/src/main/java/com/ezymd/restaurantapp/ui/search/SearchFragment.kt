package com.ezymd.restaurantapp.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.RoundedImageView
import com.ezymd.restaurantapp.details.DetailsActivity
import com.ezymd.restaurantapp.ui.home.RestaurantAdapter
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private var restaurantAdapter: RestaurantAdapter? = null
    private var isNullViewRoot = false
    private lateinit var searchViewModel: SearchViewModel
    private var viewRoot: View? = null

    private val dataResturant = ArrayList<Resturant>()

    private val userInfo by lazy {
        (activity as MainActivity).userInfo
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        isNullViewRoot = false
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_search, container, false)
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
            searchViewModel.getResturants(BaseRequest(userInfo))
            setSearchListerner()
        }

    }

    private fun setSearchListerner() {
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (search.text.toString().trim().length > 4) {
                    val baseRequest = BaseRequest(userInfo)
                    baseRequest.paramsMap.put("search", search.text.toString())
                    searchViewModel.searchRestaurants(baseRequest)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
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
            RestaurantAdapter(activity as MainActivity, OnRecyclerView { position, view ->
                val smallThumbnail = view.findViewById<RoundedImageView>(R.id.ivNotesThumb)
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(JSONKeys.OBJECT, dataResturant[position])
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as Activity?)!!, smallThumbnail, "thumbnailTransition"
                )
                (activity as MainActivity).startActivity(intent, optionsCompat.toBundle())
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

        searchViewModel.mSearchData.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
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

        searchViewModel.mResturantData.observe(this, androidx.lifecycle.Observer {
            if (it.status == ErrorCodes.SUCCESS) {
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
        searchViewModel.mResturantData.removeObservers(this)
        searchViewModel.errorRequest.removeObservers(this)
        searchViewModel.mSearchData.removeObservers(this)
    }


}