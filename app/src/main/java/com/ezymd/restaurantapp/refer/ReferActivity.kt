package com.ezymd.restaurantapp.refer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.ui.home.model.Resturant
import com.ezymd.restaurantapp.utils.*
import kotlinx.android.synthetic.main.activity_refer_earn.*
import kotlinx.android.synthetic.main.header_new.*

class ReferActivity : BaseActivity() {
    private var restaurantAdapter: ReferAdapter? = null
    private val transactionList = ArrayList<ReferModel>()
    private val viewModel by lazy {
        ViewModelProvider(this).get(ReferViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer_earn)
      //  getData()
        setToolBar()
        setHeaderData()

        setAdapter()
        setObserver()

    }

    private fun setAdapter() {
        itmesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itmesRecyclerView.addItemDecoration(
            VerticalSpacesItemDecoration(
                (resources.getDimensionPixelSize(
                    R.dimen._13sdp
                ))
            )
        )
        restaurantAdapter = ReferAdapter(this, OnRecyclerView { position, view ->

        }, transactionList)
        itmesRecyclerView.adapter = restaurantAdapter


    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        viewModel.getDetails(baseRequest)

    }

    override fun onResume() {
        super.onResume()


    }

    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {


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

    private fun setHeaderData() {


        code.setOnClickListener {
            //   https://play.google.com/store/apps/details?id=com.poppinsdigital.arms&referrer=123
            val contentToShare =
                "https://play.google.com/store/apps/details?id=$packageName&referrer=123"
            SnapLog.print(contentToShare)
            UIUtil.clickAlpha(it)
            try {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    contentToShare
                )
                startActivity(Intent.createChooser(share, "Share to"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun setToolBar() {
        headertext.visibility=View.VISIBLE
        headertext.text = getString(R.string.refer_and_earn)
        leftIcon.setOnClickListener {
            onBackPressed()
        }

        setSupportActionBar(toolbar)

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}