package com.ezymd.restaurantapp.network

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.ezymd.restaurantapp.EzymdApplication
import com.ezymd.restaurantapp.ServerConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSession

class ApiClient private constructor() {
    companion object {
        @Volatile
        private var retrofit: Retrofit? = null

        @Volatile
        private var okHttpClient: OkHttpClient? = null
        val client: Retrofit?
            get() {
                if (okHttpClient == null) {
                    synchronized(ApiClient::class.java) { initOkHttp() }
                }
                if (retrofit == null) {
                    synchronized(ApiClient::class.java) {
                        retrofit = Retrofit.Builder()
                            .baseUrl(ServerConfig.BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                    }
                }
                return retrofit
            }

        private fun initOkHttp() {
            val REQUEST_TIMEOUT = 60
            val httpClient = OkHttpClient().newBuilder()
                .hostnameVerifier { hostname: String?, session: SSLSession? -> true }
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)

            if (ServerConfig.IS_TESTING) {
                val collector = ChuckerCollector(
                    context = EzymdApplication.getInstance().applicationContext,
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_HOUR
                )

                @Suppress("MagicNumber")
                val chuckerInterceptor = ChuckerInterceptor.Builder(EzymdApplication.getInstance().applicationContext)
                    .collector(collector)
                    .maxContentLength(250_000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()

                httpClient.addInterceptor(chuckerInterceptor)
            }
            httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            okHttpClient = httpClient.build()
        }
    }

    init {
        throw RuntimeException("getClient() for instance")
    }
}