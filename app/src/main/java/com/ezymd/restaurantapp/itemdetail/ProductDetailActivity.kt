package com.ezymd.restaurantapp.itemdetail

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.cart.CartActivity
import com.ezymd.restaurantapp.customviews.ValueChangedListener
import com.ezymd.restaurantapp.details.model.ItemModel
import com.ezymd.restaurantapp.details.model.Product
import com.ezymd.restaurantapp.itemdetail.adapter.OptionsAdapter
import com.ezymd.restaurantapp.itemdetail.adapter.ProductDetailPagerAdapter
import com.ezymd.restaurantapp.itemdetail.model.ImageModel
import com.ezymd.restaurantapp.itemdetail.model.Modifier
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.cart_view.*


class ProductDetailActivity : BaseActivity() {
    private var currentProduct: ItemModel? = null
    private var stock = 0
    private val viewModel by lazy {
        ViewModelProvider(this).get(ItemDetailViewModel::class.java)
    }
    private val product by lazy {
        intent.getSerializableExtra(JSONKeys.OBJECT) as Product
    }
    private val restaurant by lazy {
        intent.getSerializableExtra(JSONKeys.RESPONSE) as Resturant
    }

    private val categoryId by lazy {
        intent.getIntExtra(JSONKeys.CATEGORY_ID, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setGUI()
        setObserver()
        fetchData()
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        EzymdApplication.getInstance().cartData.observe(this, Observer {
            if (it != null) {
                processCartData(it)
            }
        })

        viewModel.images.observe(this, Observer {
            if (it != null) {
                setBannerPager(it)
            }
        })
        viewModel.options.observe(this, Observer {
            if (it != null) {
                val restaurantAdapter = OptionsAdapter(this, it, 0.0)
                rv_modifiers?.adapter = restaurantAdapter
            }
        })
        viewModel.errorRequest.observe(this) {
            showError(false, it, null)
        }
        viewModel.isLoading.observe(this) {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.selectedOptionsList.observe(this) {
            if (viewModel.options.value != null) {
                val list = ArrayList<Modifier>(it.values)
                checkExistInCart(list)
                if (list.size > 0) {
                    stock = list.get(0).stock
                    quantityPicker.max = stock
                    if (stock == 0) {
                        add.background = null
                        add.text = getString(R.string.sold_out)
                        add.setTextColor(ContextCompat.getColor(this, R.color.red))
                    }
                }
                /*tv_price?.text = "${userInfo?.currency}${
                    CalculationUtils().getModifierPrice(
                        0.0,
                        list
                    )
                }"*/
            }

        }
        viewCart.setOnClickListener {
            val intent = Intent(this@ProductDetailActivity, CartActivity::class.java)
            intent.putExtra(JSONKeys.OBJECT, restaurant)
            startActivity(intent)
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }
    }

    private fun checkExistInCart(list: ArrayList<Modifier>) {
        SnapLog.print("checkExistInCart===============")
        if (EzymdApplication.getInstance().cartData.value == null)
            return
        val lsitOfCurrentProduct = EzymdApplication.getInstance().cartData.value!!.filter {
            (it.id == product.id)
        }
        if (lsitOfCurrentProduct == null || lsitOfCurrentProduct.isEmpty()) {
            showAddButton()
            // add button show
        } else {
            var isPresent = false
            for (item in lsitOfCurrentProduct) {
                if (item.listModifiers.size == 0) {
                    showAddButton()
                    //add button show
                } else {
                    var productVariantids = ""
                    for (modifier in item.listModifiers) {
                        productVariantids = productVariantids + "," + modifier.id
                    }
                    if (productVariantids.length > 1)
                        productVariantids = productVariantids.substring(1, productVariantids.length)

                    var selectedVariantids = ""
                    for (mModi in list) {
                        selectedVariantids = selectedVariantids + "," + mModi.id
                    }
                    if (selectedVariantids.length > 1)
                        selectedVariantids =
                            selectedVariantids.substring(1, selectedVariantids.length)

                    if (productVariantids.equals(selectedVariantids)) {
                        currentProduct = item
                        setQuantity(item.quantity)
                        isPresent = true
                        break
                    }
                }
            }
            if (!isPresent) {
                showAddButton()
            }
        }
    }

    private fun showAddButton() {
        add.post {
            add.alpha = 1.0f
            add.visibility = View.VISIBLE
            quantityPicker.visibility = View.GONE
            quantityPicker.value = 0
        }
    }

    private fun setQuantity(quantity: Int) {
        add.visibility = View.GONE
        add.alpha = 0.0f
        quantityPicker.visibility = View.VISIBLE
        quantityPicker.value = quantity

    }


    private fun processCartData(arrayList: ArrayList<ItemModel>) {
        val calc = CalculationUtils().processCartData(arrayList)
        setCartData(calc.first, calc.second)
    }

    var isExanded = false
    private fun setCartData(quantity: Int, price: Double) {
        val view = findViewById<FrameLayout>(R.id.cartView)
        if (quantity == 0 && price == 0.0) {
            view.visibility = View.GONE
            slideDown(view)
            isExanded = false
        } else {
            view.visibility = View.VISIBLE
            if (!isExanded)
                slideUp(view)
            isExanded = true
            setCartDetails(quantity, price)
        }
    }

    private fun setBannerPager(dataBanner: ArrayList<ImageModel>) {
        bannerPager.offscreenPageLimit = 1
        /* val model = ImageModel(-1, product.image!!, 0)
         dataBanner.add(0, model)
        */ if (!dataBanner.isNullOrEmpty()) {
            iv_icon.visibility = View.GONE
            val registrationTutorialPagerAdapter = ProductDetailPagerAdapter(
                this,
                dataBanner, OnRecyclerView { position, view ->
                    val bannerList = ArrayList<String>()
                    for (imageModel in dataBanner) {
                        bannerList.add(imageModel.image)
                    }
                    ShowImageVideo(this).Display(bannerList, position)


                })
            bannerPager.adapter = registrationTutorialPagerAdapter
            bannerPager.visibility = View.VISIBLE
            dots_indicator.setViewPager(bannerPager)
            dots_indicator.visibility = View.VISIBLE
        } else iv_icon?.visibility = View.VISIBLE
        setPageChangeListener()

    }

    private fun setPageChangeListener() {
        bannerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setCartDetails(quantityCount: Int, price: Double) {
        runOnUiThread(Runnable {
            quantity.text = CalculationUtils().getPriceText(this, quantityCount, price, 0.0)
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

    @SuppressLint("SetTextI18n")
    private fun setGUI() {

        tv_name?.text = product.item
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv_desc?.text = Html.fromHtml(product.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            tv_desc?.text = Html.fromHtml(product.description)
        }
        tv_menufacturer?.text = product.manufactured_by
        tv_price?.text = "${userInfo?.currency}${product.calculatedPrice}"
        if (!TextUtils.isEmpty(product.image?.firstOrNull())) {
            GlideApp.with(applicationContext)
                .load(product.image).centerCrop().override(200, 200).dontAnimate()
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_icon)
        } else {
            iv_icon?.visibility = View.GONE
        }

        leftIcon.setOnClickListener {
            UIUtil.clickAlpha(it)
            onBackPressed()
        }


        if (product.qnty != 0) {
            add.visibility = View.GONE
            quantityPicker.visibility = View.VISIBLE
            quantityPicker.increment(product.qnty)
        }

        add.setOnClickListener {
            if (stock == 0) {
                viewModel.errorRequest.value =
                    getString(R.string.this_item_is_not_available)
                return@setOnClickListener
            }
            it.animate().alpha(0f).setDuration(250).start()
            quantityPicker.alpha = 0.0f
            quantityPicker.visibility = View.VISIBLE
            quantityPicker.animate().alpha(1f).setDuration(250).start()

            quantityPicker.postDelayed(Runnable {
                add.visibility = View.GONE
                product.qnty = 1
                quantityPicker.alpha = 1f
                val list = viewModel.selectedOptionsList.value?.values ?: ArrayList()
                val item = getItemModelObject(product)
                item.uuid = viewModel.generateUUID()
                item.listModifiers.addAll(list)
                viewModel.addToCart(item)
                currentProduct = item
                // checkExistInCart(list)
                quantityPicker.increment(1)


            }, 50)
        }


        quantityPicker.setLimitExceededListener { limit, exceededValue ->

        }


        quantityPicker.valueChangedListener =
            ValueChangedListener { value, action ->
                SnapLog.print("quantity====" + value)
                if (value < 1) {
                    quantityPicker.animate().alpha(0f).setDuration(250).start()
                    currentProduct?.quantity = 0
                    add.alpha = 0.0f
                    add.visibility = View.VISIBLE
                    add.animate().alpha(1f).setDuration(250)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationEnd(p0: Animator?) {
                                add.alpha = 1.0f
                            }

                            override fun onAnimationCancel(p0: Animator?) {

                            }

                            override fun onAnimationRepeat(p0: Animator?) {

                            }

                            override fun onAnimationStart(p0: Animator?) {

                            }
                        })
                        .setUpdateListener { animation ->
                            add.alpha =
                                (250 / if (animation!!.currentPlayTime <= 20) 1 else animation.currentPlayTime).toFloat()
                        }.start()
                    quantityPicker.value = 0
                    viewModel.removeItem(currentProduct!!.uuid)
                } else {

                    currentProduct?.quantity = value
                    viewModel.addToCart(currentProduct!!)
                }

            }
    }

    private fun fetchData() {
        val baseRequest = BaseRequest(userInfo)
        baseRequest.paramsMap["product_id"] = product.id.toString()
        viewModel.getProductDetails(baseRequest)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(JSONKeys.CATEGORY_ID, categoryId)
        intent.putExtra(JSONKeys.OBJECT, product)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

}