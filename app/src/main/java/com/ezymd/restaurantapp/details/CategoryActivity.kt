package com.ezymd.restaurantapp.details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.CartActivity
import com.ezymd.restaurantapp.customviews.SnapTextView
import com.ezymd.restaurantapp.dashboard.model.DataTrending
import com.ezymd.restaurantapp.databinding.ActivityCategoriesBinding
import com.ezymd.restaurantapp.details.adapter.SubCategoryWithProductAdapter
import com.ezymd.restaurantapp.details.model.*
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.cart_view.*
import kotlinx.android.synthetic.main.content_scrolling_category.*

class CategoryActivity : BaseActivity() {
    private val bannerList = ArrayList<String>()
    private var adapter: SubCategoryWithProductAdapter? = null
    private var isDisplayCount = false
    private var selectedStudentPosition = 0
    private val dataResturant = ArrayList<ItemModel>()
    private val dataHeader = ArrayList<Header>()
    private var mData = CategoriesAndBannersData()
    private val foodType = ArrayList<FoodTypeModel>()
    private val viewModel by lazy {
        ViewModelProvider(this).get(CategoryViewModel::class.java)
    }
    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as DataTrending
    }


    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState: State = State.IDLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        getData()
        setToolBar()
        setHeaderData()

        setAdapter()
        setObserver()

    }

    private fun setAdapter() {
        itmesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



        itmesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubCategoryWithProductAdapter(this, dataHeader, viewModel,
            OnRecyclerViewClickType { position, childposition, view ->


            })
        itmesRecyclerView.adapter = adapter

    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["shop_id"] = "" + restaurant.id
        viewModel.loadShopDetail(baseRequest)

    }

    private fun setObserver() {

        EzymdApplication.getInstance().cartData.observe(this, Observer {
            if (it != null) {
                checkDataChanges(it)
                processCartData(it)
            }
        })


        viewModel.mCategoryWithProduct.observe(this, {
            if (it.data != null) {

            }
        })
        viewModel.mSubCategoriesResponse.observe(this, {
            if (it.data != null) {
                it.data?.let { it1 ->
                    dataHeader.clear()
                    dataHeader.addAll(it1)
                    adapter?.notifyDataSetChanged()
                }
            }
        })
        viewModel.mResturantData.observe(this, {
            if (it.data != null) {
                mData = it.data!!
                bannerList.clear()
                bannerList.add(restaurant.banner)
                for (item in it.data?.banners!!) {
                    bannerList.add(item.banner!!)
                }
                if (it.data?.categories?.isNullOrEmpty() == false)
                    detailContent.visibility = View.VISIBLE
                processDataFindTabs(it.data!!)

            }
        })
        viewModel.errorRequest.observe(this, {
            showError(false, it, null)
        })
        viewModel.isLoading.observe(this, {
            progress.visibility = if (it) View.VISIBLE else View.GONE


        })

        image.setOnClickListener {
            UIUtil.clickAlpha(it)
            if (bannerList.size > 0)
                ShowImageVideo(this).Display(bannerList, 0)
        }

        viewCart.setOnClickListener {
            val intent = Intent(this@CategoryActivity, CartActivity::class.java)
            intent.putExtra(JSONKeys.OBJECT, getRestaurantObject(restaurant))
            startActivity(intent)
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }


    }


    private fun checkDataChanges(it: ArrayList<ItemModel>) {
        var i = 0
        for (item in dataResturant) {
            SnapLog.print("data quantity===" + item.quantity)
            if (it.size == 0)
                item.quantity = 0
            dataResturant[i] = item
            for (itemModel in it) {
                if (itemModel.id == item.id) {
                    SnapLog.print("quantity===" + itemModel.quantity)
                    dataResturant[i] = itemModel
                    break
                }
            }
            i++
        }

    }

    private fun processCartData(arrayList: ArrayList<ItemModel>) {
        var quantity = 0
        var price = 0
        for (itemModel in arrayList) {
            price += (itemModel.price * itemModel.quantity)
            quantity += itemModel.quantity
        }


        setCartData(quantity, price)

    }

    var isExanded = false
    private fun setCartData(quantity: Int, price: Int) {

        if (quantity == 0 && price == 0) {
            //cartView.visibility= View.GONE
            slideDown(cartView)
            isExanded = false
        } else {
            // cartView.visibility= View.VISIBLE
            if (!isExanded)
                slideUp(cartView)
            isExanded = true
            setCartDetails(quantity, price)
        }


    }

    private fun setCartDetails(quantityCount: Int, price: Int) {
        runOnUiThread(Runnable {
            quantity.text = TextUtils.concat(
                "" + quantityCount,
                " ",
                getString(R.string.items),
                " | ",
                getString(R.string.dollor),
                "" + price
            )
        })

    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f,                 // fromXDelta
            0f,                 // toXDelta
            view.height.toFloat(),  // fromYDelta
            0f
        )             // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    private fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,                 // fromXDelta
            0f,                 // toXDelta
            0f,                 // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
            }
        })
        view.startAnimation(animate)
    }

    private fun processDataFindTabs(it: CategoriesAndBannersData) {
        foodType.clear()
        if (!it.categories.isNullOrEmpty()) {
            for (item in it.categories!!) {
                val model = FoodTypeModel()
                model.categoryID = item.id
                model.categoryName = item.name
                var isPresent = false
                for (foodCategory in foodType) {
                    if (foodCategory.categoryID == model.categoryID) {
                        foodCategory.count = foodCategory.count + 1
                        isPresent = true
                    }
                }
                if (!isPresent)
                    foodType.add(model)

            }
        }
        disPlayCategoryData()
    }

    private fun disPlayCategoryData() {
        if (foodType.size == 0) {
            layoutTabs.visibility = View.GONE
            return
        }
        layoutTabs.visibility = View.VISIBLE
        tabs.clearOnTabSelectedListeners()
        tabs.removeAllTabs()
        tabs.visibility = View.VISIBLE
        for (i in 0 until foodType.size) {
            val studentName = SnapTextView(this)
            studentName.typeface = CustomTypeFace.medium
            studentName.setSingleLine()
            studentName.letterSpacing = 0.01f
            studentName.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._13sdp).toFloat()
            )


            studentName.text = if (isDisplayCount) TextUtils.concat(
                foodType[i].categoryName,
                " (" + foodType[i].count + ")"
            ) else foodType[i].categoryName
            studentName.setTextColor(ContextCompat.getColor(this, R.color.color_787a7f))
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
                                this@CategoryActivity,
                                R.color.color_002366
                            )
                        )
                        for (j in 0 until tabs.tabCount) {
                            if (j != selectedStudentPosition) {
                                val unSelView = tabs.getTabAt(j)!!.customView as SnapTextView?
                                unSelView!!.setTextColor(
                                    ContextCompat.getColor(
                                        this@CategoryActivity,
                                        R.color.color_787a7f
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
        if (foodType.size == 0)
            return
        val category = foodType[selectedStudentPosition]
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["shop_id"] = "" + restaurant.id
        baseRequest.paramsMap["category_id"] = "" + category.categoryID
        viewModel.loadShopCategoryWithProduct(baseRequest)
    }

    private fun setHeaderData() {

        GlideApp.with(applicationContext)
            .load(restaurant.banner).centerCrop().override(550, 350).dontAnimate()
            .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(image)

        toolbar_layout.title = restaurant.name
        name.text = restaurant.name
        address.text = restaurant.address
        category.text = restaurant.cuisines
        distance.text = TextUtils.concat("" + UIUtil.round(restaurant.distance, 1), " km")
        rating.text = if (restaurant.rating > 0) "" + restaurant.rating else "N/A"
        minimumOrder.text =
            if (restaurant.min_order.equals("0")) "N/A" else "min " + getString(R.string.dollor) + restaurant.min_order

        counts.setOnClickListener {
            isDisplayCount = !isDisplayCount
            disPlayCategoryData()

        }
        share.setOnClickListener {
            UIUtil.clickAlpha(it)
            try {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, restaurant.name)
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    restaurant.address + "\n" + restaurant.lat + ", " + restaurant.lang
                )
                startActivity(Intent.createChooser(share, "Share Shop"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        address.setOnClickListener {
            UIUtil.clickAlpha(it)
            val gmmIntentUri = Uri.parse("geo:${restaurant.lat},${restaurant.lang}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolBar() {

        call.setOnClickListener {
            UIUtil.clickAlpha(it)
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + restaurant.phone_no)
            startActivity(intent)
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        window.statusBarColor = Color.TRANSPARENT

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (Math.abs(verticalOffset) != verticalOffset) {
                    toolbar_layout.setCollapsedTitleTextColor(Color.TRANSPARENT)
                }

                if (mCurrentState != State.EXPANDED) {
                }
                mCurrentState = State.EXPANDED
            } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    toolbar.setTitleTextColor(Color.BLACK)
                    toolbar.setBackgroundColor(Color.WHITE)
                    window.statusBarColor = Color.WHITE
                    toolbar_layout.setCollapsedTitleTextColor(Color.BLACK)
                }
                mCurrentState = State.COLLAPSED
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    fun showBottomSheet(it: View, item: Product) {
        UIUtil.clickAlpha(it)
        val sheetDialog = BottomSheetDialog(this)
        sheetDialog.setContentView(R.layout.tnc_bottom_sheet)
        val promoName = sheetDialog.findViewById<TextView>(R.id.name)
        promoName!!.text = item.item
        val description = sheetDialog.findViewById<TextView>(R.id.description)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            description!!.text = Html.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            description!!.text = Html.fromHtml(item.description)
        }
        sheetDialog.setCanceledOnTouchOutside(true)
        sheetDialog.show()
    }

    /*private class ChildVH(val binding: CountChildrenItemBinding) :
        ExpandableAdapter.ViewHolder(binding.root)

    private class ParentVH(val binding: CountParentItemBinding) :
        ExpandableAdapter.ViewHolder(binding.root)

    private class SubCategories : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {

        private var header: List<Header>? = null

        fun setNewData(categories: List<Header>) {
            this.header = categories
            notifyDataSetChanged()
        }


        override fun onCreateGroupViewHolder(
            viewGroup: ViewGroup,
            viewType: Int
        ): ViewHolder = LayoutInflater.from(viewGroup.context)
            .let { CountParentItemBinding.inflate(it, viewGroup, false) }
            .let(::ParentVH)

        override fun onCreateChildViewHolder(
            viewGroup: ViewGroup,
            viewType: Int
        ): ViewHolder = LayoutInflater.from(viewGroup.context)
            .let { CountChildrenItemBinding.inflate(it, viewGroup, false) }
            .let(::ChildVH)


        override fun onBindChildViewHolder(
            holder: ViewHolder,
            groupPosition: Int,
            childPosition: Int,
            payloads: List<Any>
        ) {
            if (payloads.isEmpty()) {
                (holder as? ChildVH)?.apply {
                    binding.dishName.text =
                        header?.getOrNull(groupPosition)?.childs?.getOrNull(childPosition)?.name
                }
            }
        }

        override fun onBindGroupViewHolder(
            holder: ViewHolder,
            groupPosition: Int,
            expand: Boolean,
            payloads: List<Any>
        ) {
            if (payloads.isEmpty()) {
                (holder as? ParentVH)?.apply {
                    binding.titleText.text = header?.getOrNull(groupPosition)?.name
                    binding.arrowImage.rotation = if (expand) 0f else 90.0f
                }
            }
        }


        override fun onGroupViewHolderExpandChange(
            holder: ViewHolder,
            groupPosition: Int,
            animDuration: Long,
            expand: Boolean
        ) {

            holder as ParentVH
            val arrowImage = holder.binding.arrowImage

                    .setDuration(animDuration)
                    .start()
                // 不要使用这种动画，Item离屏之后，动画会取消
//            arrowImage.animate()
//                .setDuration(animDuration)
//                .rotation(0f)
//                .start()
            } else {
                ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 90f)
                    .setDuration(animDuration)
                    .start()
            }

        }

        override fun getGroupCount(): Int = header?.size ?: 0
        override fun getChildCount(groupPosition: Int): Int =
            header?.get(groupPosition)?.products?.size ?: 0
    }*/
}