package com.ezymd.restaurantapp.login

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.login.otp.OTPScreen
import com.ezymd.restaurantapp.utils.*
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
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

        next.setOnClickListener {
            SuspendKeyPad.suspendKeyPad(this)
            UIUtil.clickHandled(it)
            loginViewModel.generateOtp(phoneNo.text.toString())
        }
        loginViewModel.isLoading.observe(this, Observer {

        })

        loginViewModel.otpResponse.observe(this, Observer {
            if (it.isStatus != ErrorCodes.SUCCESS) {
                showError(false, it.message, null)
            } else {
                startActivityForResult(
                    Intent(
                        this,
                        OTPScreen::class.java
                    ).putExtra(JSONKeys.MOBILE_NO, phoneNo.text.toString().trim()),
                    JSONKeys.OTP_REQUEST
                )
            }
        })

        loginViewModel.loginResponse.observe(this, Observer {

            if (it.status == ErrorCodes.SUCCESS) {
                setLoginUser(it)

            } else {
                showError(false, it.message, null)
            }
        })
        loginViewModel.loginRequest.observe(this, Observer {
            if (it.error) {
                showError(false, it.errorMessage, null)
            } else {
                loginViewModel.login(it)
            }
        })
        loginViewModel.showError().observe(this, Observer {
            showError(false, it, null)
        })

        loginViewModel.isLoading.observe(this, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun setLoginUser(it: LoginModel) {
        userInfo?.accessToken = it.data.access_token
        userInfo?.userName = it.data.user.name
        userInfo?.email = it.data.user.email
        userInfo?.userID = it.data.user.id
        userInfo?.phoneNumber = it.data.user.phone_no
        userInfo?.profilePic = it.data.user.profile_pic
        startActivity(Intent(this, MainActivity::class.java))
        finish()

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
        if (requestCode == RC_SIGN_IN) {
            processGoogleData(data)
        } else if (requestCode == JSONKeys.OTP_REQUEST && resultCode == Activity.RESULT_OK) {
            this.finish()
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun processGoogleData(data: Intent?) {
        val result = GoogleSignIn.getSignedInAccountFromIntent(data)
        loginViewModel.loginGoogle(result)


    }

    fun printKeyHash(context: Activity): String? {
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
                SnapLog.print("Key Hash=" + key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }
}