package com.ezymd.restaurantapp.login

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.ezymd.restaurantapp.ServerConfig
import com.ezymd.restaurantapp.network.ApiClient
import com.ezymd.restaurantapp.network.NetworkCommonRequest
import com.ezymd.restaurantapp.network.ResultWrapper
import com.ezymd.restaurantapp.network.WebServices
import com.ezymd.restaurantapp.utils.BaseResponse
import com.ezymd.restaurantapp.utils.SnapLog
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineDispatcher
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginRepository private constructor() {


    suspend fun generateOtp(
        otp: String,
        dispatcher: CoroutineDispatcher
    ): ResultWrapper<BaseResponse> {

        SnapLog.print("Login repositry=====")
        val apiServices = ApiClient.client!!.create(WebServices::class.java)
        val map = HashMap<String, String>()


        return NetworkCommonRequest.instance.safeApiCall(dispatcher) {
            apiServices.getOutStandingAmount(
                ServerConfig.BASE_URL,
                map
            )
        }


    }

     fun fbLogin(accessToken: AccessToken?, result: MutableLiveData<LoginRequest>) {
        val request = GraphRequest.newMeRequest(
            accessToken, object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(jsonObject: JSONObject?, response: GraphResponse?) {
                    if (response?.error == null) {
                        SnapLog.print(jsonObject.toString())
                        try {
                            val loginModel = LoginRequest()
                            loginModel.first_name = jsonObject!!.getString("first_name")
                            loginModel.last_name = jsonObject.getString("last_name")
                            loginModel.email = jsonObject.getString("email")
                            loginModel.id = jsonObject.getString("id")
                            loginModel.image_url =
                                "https://graph.facebook.com/${loginModel.id}/picture?type=normal"
                            loginModel.socialLogin = true
                            result.postValue(loginModel)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            val loginModel = LoginRequest()
                            loginModel.errorMessage = e.message
                            loginModel.error = true
                            result.postValue(loginModel)
                        }


                    } else {
                        val loginModel = LoginRequest()
                        loginModel.errorMessage = response.error.errorMessage
                        loginModel.error = true
                        result.postValue(loginModel)
                    }
                }

            })

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    fun googleLogin(signInResult: Task<GoogleSignInAccount>, result: MutableLiveData<LoginRequest>) {
        try {
            val account: GoogleSignInAccount? = signInResult.getResult(ApiException::class.java)
            val loginModel = LoginRequest()
            if (account!=null) {
                loginModel.first_name = account.displayName
                loginModel.last_name = account.familyName
                loginModel.email =account.email
                loginModel.id = account.id
                loginModel.image_url = account.photoUrl.toString()
                loginModel.socialLogin = true
            }else{
                loginModel.errorMessage ="Account Info Not Found"
                loginModel.error = true

            }
            result.postValue(loginModel)
        } catch (e: ApiException) {
            SnapLog.print( "signInResult:failed code=" + e.statusCode)
            val loginModel = LoginRequest()
            loginModel.errorMessage =e.localizedMessage
            loginModel.error = true
            result.postValue(loginModel)

        }


    }



    companion object {
        @Volatile
        private var sportsFeeRepository: LoginRepository? = null

        @JvmStatic
        val instance: LoginRepository?
            get() {
                if (sportsFeeRepository == null) {
                    synchronized(LoginRepository::class.java) {
                        sportsFeeRepository = LoginRepository()
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
