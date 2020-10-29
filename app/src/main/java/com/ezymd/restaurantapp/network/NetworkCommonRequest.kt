package com.ezymd.restaurantapp.network

import com.ezymd.restaurantapp.utils.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class NetworkCommonRequest {

    companion object {
        @Volatile
        private var networkRequest: NetworkCommonRequest? = null

        @JvmStatic
        val instance: NetworkCommonRequest?
            get() {
                if (networkRequest == null) {
                    synchronized(NetworkCommonRequest::class.java) {
                        networkRequest = NetworkCommonRequest()
                    }
                }
                return networkRequest
            }
    }

    init {
        if (networkRequest != null) {
          //  throw RuntimeException("Use getInstance() method to get the single instance of NetworkCommonRequest class.")
        }
    }

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.let {
                val gsonAdapter = Gson().getAdapter(ErrorResponse::class.java)
                gsonAdapter.fromJson(it.charStream())


            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }


}