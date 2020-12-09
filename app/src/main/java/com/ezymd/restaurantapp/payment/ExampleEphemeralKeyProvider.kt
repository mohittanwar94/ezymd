package com.ezymd.restaurantapp.payment

import android.content.Context
import androidx.annotation.Size
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.SnapLog
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExampleEphemeralKeyProvider @JvmOverloads constructor(
    private val stripeAccountId: String? = null
) : EphemeralKeyProvider {
    private val workContext = Dispatchers.IO
    val apiServices = ApiClient.client!!.create(WebServices::class.java)

    override fun createEphemeralKey(
        @Size(min = 4) apiVersion: String,
        keyUpdateListener: EphemeralKeyUpdateListener
    ) {
        val params = hashMapOf("api_version" to apiVersion)
        stripeAccountId?.let {
            params["stripe_account"] = it
        }

        CoroutineScope(workContext).launch {
            val response =
                kotlin.runCatching {
                    apiServices
                        .createEphemeralKey(params)
                        .string()
                }

            withContext(Dispatchers.Main) {
                response.fold(
                    onSuccess = {
                        keyUpdateListener.onKeyUpdate(it)
                        SnapLog.print(it)
                    },
                    onFailure = {
                        keyUpdateListener
                            .onKeyUpdateFailure(0, it.message.orEmpty())
                    }
                )
            }
        }
    }
}