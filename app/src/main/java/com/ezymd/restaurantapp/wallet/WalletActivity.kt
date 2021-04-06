package com.ezymd.restaurantapp.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.refer.ReferAdapter
import com.ezymd.restaurantapp.refer.ReferViewModel
import com.ezymd.restaurantapp.refer.Transaction
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.ErrorCodes
import com.ezymd.restaurantapp.utils.VerticalSpacesItemDecoration
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.header_new.*

class WalletActivity : BaseActivity() {
    private var restaurantAdapter: ReferAdapter? = null
    private val transactionList = ArrayList<Transaction>()
    private val viewModel by lazy {
        ViewModelProvider(this).get(ReferViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        getData()
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
                    R.dimen._10sdp
                ))
            )
        )
        restaurantAdapter = ReferAdapter(this, transactionList)
        itmesRecyclerView.adapter = restaurantAdapter


    }

    private fun getData() {
        val baseRequest = BaseRequest(userInfo)
        viewModel.getDetails(baseRequest)

    }

    override fun onResume() {
        super.onResume()


    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        viewModel.mResturantData.observe(this, Observer {
            if (it.status == ErrorCodes.SUCCESS) {
                balance.text = getString(R.string.dollor) + "" + it.data?.total
                transactionList.addAll(it.data!!.history)
                restaurantAdapter?.setData(transactionList)
            } else {
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


    override fun onStop() {
        super.onStop()

    }

    private fun setHeaderData() {


    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    private fun setToolBar() {
        headertext.visibility = View.VISIBLE
        headertext.text = getString(R.string.my_wallet)
        leftIcon.setOnClickListener {
            onBackPressed()
        }

        setSupportActionBar(toolbar)

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}