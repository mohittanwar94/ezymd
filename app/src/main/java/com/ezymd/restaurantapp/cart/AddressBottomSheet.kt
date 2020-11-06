package com.ezymd.restaurantapp.cart

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.location.LocationActivity
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.OnRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet_address.*

class AddressBottomSheet : BottomSheetDialogFragment() {

    private var onClickListener: OnRecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_address, container, false)
    }

    fun setOnButtonClickListener(clickListener: OnRecyclerView) {
        onClickListener = clickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGUI()
    }

    private fun setGUI() {
        done.alpha = 0.5f
        done.isEnabled = false
        done.setOnClickListener {
            val builder = StringBuilder()
            builder.append(flatNo.text.toString().trim())
            builder.append(", ")
            builder.append(floor.text.toString().trim())
            if (tower.text.toString().isNotEmpty()) {
                builder.append(", ")
                builder.append(tower.text.toString().trim())
            }
            if (howToReach.text.toString().isNotEmpty()) {
                builder.append(", ")
                builder.append(howToReach.text.toString().trim())
            }
            builder.append(", ")
            builder.append(toLocationTxt.text.toString().trim())
            (activity as LocationActivity).updateAddress(builder.toString())

        }
        toLocationTxt.setText(arguments?.getString(JSONKeys.LOCATION_OBJECT))
        flatNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (flatNo.text.toString().trim().isNotEmpty() && floor.text.toString().trim()
                        .isNotEmpty()
                ) {
                    done.alpha = 1f
                    done.isEnabled = true
                } else {
                    done.alpha = 0.5f
                    done.isEnabled = false
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        floor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //android:background="@drawable/ic_gray_btn_pressed"

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (flatNo.text.toString().trim().isNotEmpty() && floor.text.toString().trim()
                        .isNotEmpty()
                ) {
                    done.alpha = 1f
                    done.isEnabled = true
                } else {
                    done.alpha = 0.5f
                    done.isEnabled = false
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }


    companion object {
        fun newInstance(location: String): AddressBottomSheet {
            val frag = AddressBottomSheet()
            val args = Bundle()
            args.putString(JSONKeys.LOCATION_OBJECT, location)
            frag.arguments = args
            return frag
        }
    }
}