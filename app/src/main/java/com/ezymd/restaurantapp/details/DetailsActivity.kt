package com.ezymd.restaurantapp.details

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.details.model.FoodTypeModel
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.MenuItemModel
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_scrolling.*

class DetailsActivity : BaseActivity() {
    private var isDisplayCount = false
    private var selectedStudentPosition = 0
    private val dataResturant = ArrayList<ItemModel>()
    private var mData = MenuItemModel()
    private val foodType = ArrayList<FoodTypeModel>()
    private var restaurantAdapter: MenuAdapter? = null
    private val viewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }
    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Resturant
    }

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        window.sharedElementEnterTransition =
            TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation);
        image.transitionName = "thumbnailTransition";

        getData()
        setToolBar()
        setHeaderData()

        setAdapter()
    }

    private fun setAdapter() {
        itmesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itmesRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                UIUtil.convertDpToPx(
                    this,
                    resources.getDimension(R.dimen._3sdp)
                )
                    .toInt()
            )
        )
        restaurantAdapter = MenuAdapter(this, OnRecyclerView { position, view ->

        }, dataResturant)
        itmesRecyclerView.adapter = restaurantAdapter


    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap.put("id", "" + restaurant.id)
        viewModel.getDetails(baseRequest)
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {

            if (it.data != null) {
                mData = it
                if (it.data.size > 0)
                    detailContent.visibility = View.VISIBLE
                processDataFindTabs(it)
            }
        })


        viewModel.errorRequest.observe(this, Observer {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })
    }

    private fun processDataFindTabs(it: MenuItemModel) {


        for (item in it.data) {
            val model = FoodTypeModel()
            model.categoryID = item.category_id
            model.categoryName = item.category
            var isPresent = false
            for (foodCategory in foodType) {
                if (foodCategory.categoryID == model.categoryID) {
                    model.count = foodCategory.count + 1
                    isPresent = true
                } else {
                    model.count = model.count + 1
                }
            }
            if (!isPresent)
                foodType.add(model)

        }

        disPlayCategoryData()
    }

    private fun disPlayCategoryData() {
        if (foodType.size == 0) {
            layoutTabs.visibility = View.VISIBLE
            return
        }
        tabs.clearOnTabSelectedListeners()
        tabs.removeAllTabs()
        tabs.visibility = View.VISIBLE
        for (i in 0 until foodType.size) {
            val studentName = SnapTextView(this)
            studentName.typeface = CustomTypeFace.medium
            studentName.setSingleLine()
            studentName.letterSpacing = 0.01f
            studentName.textSize = UIUtil.convertDpToPx(
                this,
                resources.getDimension(R.dimen._2sdp)
            )

            studentName.text = if (isDisplayCount) TextUtils.concat(
                foodType[i].categoryName,
                " (" + foodType[i].count + " )"
            ) else foodType[i].categoryName
            studentName.setTextColor(ContextCompat.getColor(this, R.color.color_667ba3))
            if (i == selectedStudentPosition) {
                studentName.setTextColor(ContextCompat.getColor(this, R.color.color_002366))
                tabs.addTab(tabs.newTab().setCustomView(studentName), true)
            } else
                tabs.addTab(tabs.newTab().setCustomView(studentName))
        }
        tabs.post {
            tabs.getTabAt(selectedStudentPosition)!!.select()
            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (selectedStudentPosition != tab.position) {
                        selectedStudentPosition = tab.position
                        val selView = tab.customView as SnapTextView?
                        selView!!.setTextColor(
                            ContextCompat.getColor(
                                this@DetailsActivity,
                                R.color.color_002366
                            )
                        )
                        for (j in 0 until tabs.tabCount) {
                            if (j != selectedStudentPosition) {
                                val unSelView = tabs.getTabAt(j)!!.customView as SnapTextView?
                                unSelView!!.setTextColor(
                                    ContextCompat.getColor(
                                        this@DetailsActivity,
                                        R.color.color_667ba3
                                    )
                                )
                            }
                        }
                        onChildChanged()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        onChildChanged()
    }

    private fun onChildChanged() {
        dataResturant.clear()
        restaurantAdapter!!.notifyDataSetChanged()
        val category = foodType[selectedStudentPosition]
        for (item in mData.data) {
            if (category.categoryID == item.category_id)
                dataResturant.add(item)

        }
        restaurantAdapter!!.setData(dataResturant)
    }

    private fun setHeaderData() {

        GlideApp.with(applicationContext)
            .load(restaurant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(image)

        toolbar_layout.title = restaurant.name
        name.text = restaurant.name
        category.text = restaurant.category
        distance.text = TextUtils.concat("" + UIUtil.round(restaurant.distance, 1), " km")
        rating.text = if (restaurant.rating > 0) "" + restaurant.rating else "N/A"
        minimumOrder.text =
            if (restaurant.minOrder.equals("0")) "N/A" else "min "+restaurant.minOrder + getString(
                R.string.dollor
            )

        counts.setOnClickListener {
            isDisplayCount = !isDisplayCount
            disPlayCategoryData()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
        window.statusBarColor = Color.TRANSPARENT

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (Math.abs(verticalOffset) != verticalOffset) {
                    toolbar_layout.setCollapsedTitleTextColor(Color.TRANSPARENT)
                }

                if (mCurrentState != State.EXPANDED) {
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar.setTitleTextColor(Color.BLACK)
                    toolbar.setBackgroundColor(Color.WHITE)
                    window.statusBarColor = Color.WHITE
                    toolbar_layout.setCollapsedTitleTextColor(Color.BLACK)
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    toolbar.setTitleTextColor(Color.TRANSPARENT)
                    window.statusBarColor = Color.TRANSPARENT
                }
                mCurrentState = State.IDLE
            }

        })

    }
}