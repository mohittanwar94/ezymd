package com.ezymd.restaurantapp.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.font.CustomTypeFace
import com.ezymd.restaurantapp.utils.JSONKeys
import kotlinx.android.synthetic.main.activity_add_delivery_instruction.*

class AddDeliveryInstruction : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_delivery_instruction)
        setToolBar()
        setHeaderData()
    }

    override fun onResume() {
        super.onResume()
        deliveryInstruction.requestFocus()
    }

    private fun setHeaderData() {
        toolbar_layout.setExpandedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.setCollapsedTitleTypeface(CustomTypeFace.bold)
        toolbar_layout.title = getString(R.string.add_delivery_instruction)

    }

    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))


        done.setOnClickListener {
            val intent = Intent()
            intent.putExtra(JSONKeys.DESCRIPTION, deliveryInstruction.text.toString().trim())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        deliveryInstruction.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                charterCount.text = "" + deliveryInstruction.text.toString().length + "/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}