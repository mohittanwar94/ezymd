package com.ezymd.restaurantapp.location.places

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.JSONKeys
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.activity_auto_complete.*
import kotlinx.android.synthetic.main.header_new.*
import java.util.*

class AutoCompleteActivity : BaseActivity() {
    var adapter: AutoCompleteAdapter? = null
    var placesClient: PlacesClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_complete)
        val apiKey = getString(R.string.google_maps_key)
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        placesClient = Places.createClient(this)
        initAutoCompleteTextView()
        setToolBar()
    }


    private fun setToolBar() {

        setSupportActionBar(findViewById(R.id.toolbar))
        headertext.text = getString(R.string.search_location)
        headertext.visibility=View.VISIBLE
        leftIcon.setOnClickListener {
            onBackPressed()
        }

    }

    private fun initAutoCompleteTextView() {
        list.onItemClickListener = autocompleteClickListener
        adapter = AutoCompleteAdapter(this, placesClient!!)
        list.adapter = adapter
        val search = findViewById<EditText>(R.id.search)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter!!.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private val autocompleteClickListener = OnItemClickListener { adapterView, view, i, l ->
        try {
            val item = adapter!!.getItem(i)
            var placeID: String?
            placeID = item.placeId

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.
            val placeFields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG
            )
            var request: FetchPlaceRequest? = null
            request = FetchPlaceRequest.builder(placeID, placeFields)
                .build()
            placesClient!!.fetchPlace(request).addOnSuccessListener { task ->

                val intentObj = Intent().putExtra(JSONKeys.OBJECT, task.place)
                setResult(Activity.RESULT_OK, intentObj)
                onBackPressed()
            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    showError(false, e.message, null)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }
}