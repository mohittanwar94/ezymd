package com.ezymd.restaurantapp.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.ybs.countrypicker.CountryPicker
import kotlinx.android.synthetic.main.login.*
import java.util.*


class Login : BaseActivity() {
    private var counCode: String="+1"
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
        countryCode.setOnClickListener {
            UIUtil.clickAlpha(it)
            selectCountry()

        }
        facebook.setOnClickListener {
            fbLogin(it)
        }
        google.setOnClickListener {
            signIn()
        }

        next.setOnClickListener {
            SuspendKeyPad.suspendKeyPad(this)
            UIUtil.clickHandled(it)
            loginViewModel.generateOtp(phoneNo.text.toString(),counCode)
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

    private fun selectCountry() {
        val picker = CountryPicker.newInstance("Choose Your Country") // dialog title

        picker.setListener { name, code, dialCode, flagDrawableResID ->
            countryCode.text=dialCode
            counCode=code
            picker.dismissAllowingStateLoss()
        }
        picker.show(supportFragmentManager, "COUNTRY_PICKER")
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
        } else if (requestCode == JSONKeys.OTP_REQUEST && resultCode != Activity.RESULT_OK) {
            SnapLog.print("back==========")
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun processGoogleData(data: Intent?) {
        val result = GoogleSignIn.getSignedInAccountFromIntent(data)
        loginViewModel.loginGoogle(result)


    }


}