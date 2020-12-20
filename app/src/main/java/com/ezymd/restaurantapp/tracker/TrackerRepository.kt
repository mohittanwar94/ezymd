package com.ezymd.restaurantapp.tracker

import com.ezymd.restaurantapp.location.model.LocationModel
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseRequest
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap


class TrackerRepository private constructor() {


    suspend fun downloadRouteInfo(
        url: ConcurrentHashMap<String, String>,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<JsonObject> {

        SnapLog.print("track repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.downloadRoute(
                url.get("origin")!!,
                url.get("sensor")!!,
                url.get("destination")!!,
                url.get("mode")!!,
                url.get("key")!!
            )
        }


    }

    suspend fun updateCoordinates(
        baseRequest: BaseRequest,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<LocationModel> {

        SnapLog.print("track repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)

        return NetworkCommonRequest.instance!!.safeApiCall(dispatcher) {
            apiServices.locationUpdates(
               baseRequest.paramsMap["id"]!!, baseRequest.accessToken
            )
        }


    }

    companion object {
        @Volatile
        private var sportsFeeRepository: TrackerRepository? = null

        @JvmStatic
        val instance: TrackerRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(TrackerRepository::class.java) {
                        sportsFeeRepository = TrackerRepository()
                    }
                }
                return sportsFeeRepository
            }
    }

    init {
        if (sportsFeeRepository != null) {
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }
}
