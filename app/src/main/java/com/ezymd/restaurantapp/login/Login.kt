package com.ezymd.restaurantapp.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.utils.ShowDialog
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UIUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.login.*
import java.util.*


class Login : BaseActivity() {
    private val RC_SIGN_IN = 100
    val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    val mGoogleApiClient by lazy {
        GoogleSignIn.getClient(this, gso);
    }
    lateinit var callbackManager: CallbackManager
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setEventListener()

    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleApiClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun setEventListener() {
        facebook.setOnClickListener {
            fbLogin(it)
        }
        google.setOnClickListener {
            signIn()
        }
        loginViewModel.isLoading.observe(this, Observer {

        })

        loginViewModel.data.observe(this, Observer {
            if (it.error) {
                showError(false, it.errorMessage, null)
            } else {

            }
        })
        loginViewModel.showError().observe(this, Observer {
            showError(false, it, null)
        })
    }

    private fun fbLogin(it: View?) {
        UIUtil.clickAlpha(it)
        loginViewModel.startLoading(true)
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .logInWithReadPermissions(this@Login, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    SnapLog.print(loginResult!!.accessToken!!.token)
                    loginViewModel.loginFb(loginResult.accessToken)
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            processGoogleData(data)
        } else if (requestCode == RC_SIGN_IN) {
            ShowDialog(this).disPlayDialog(R.string.unable_to_login, false, false)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun processGoogleData(data: Intent?) {
        val result = GoogleSignIn.getSignedInAccountFromIntent(data)
        loginViewModel.loginGoogle(result)


    }

    /* fun printKeyHash(context: Activity): String? {
         val packageInfo: PackageInfo
         var key: String? = null
         try {
             //getting application package name, as defined in manifest
             val packageName = context.applicationContext.packageName

             //Retriving package info
             packageInfo = context.packageManager.getPackageInfo(
                 packageName,
                 PackageManager.GET_SIGNATURES
             )
             Log.e("Package Name=", context.applicationContext.packageName)
             for (signature in packageInfo.signatures) {
                 val md: MessageDigest = MessageDigest.getInstance("SHA")
                 md.update(signature.toByteArray())
                 key = String(Base64.encode(md.digest(), 0))

                 // String key = new String(Base64.encodeBytes(md.digest()));
                 Log.e("Key Hash=", key)
             }
         } catch (e1: PackageManager.NameNotFoundException) {
             Log.e("Name not found", e1.toString())
         } catch (e: NoSuchAlgorithmException) {
             Log.e("No such an algorithm", e.toString())
         } catch (e: Exception) {
             Log.e("Exception", e.toString())
         }
         return key
     }*/
}