package com.ezymd.restaurantapp.login.otp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Vibrator
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ezymd.restaurantapp.BaseActivity
import com.ezymd.restaurantapp.MainActivity
import com.ezymd.restaurantapp.OtpBroadcastReceiver
import com.ezymd.restaurantapp.OtpBroadcastReceiver.OTPReceiveListener
import com.ezymd.restaurantapp.R
import com.ezymd.restaurantapp.login.LoginRequest
import com.ezymd.restaurantapp.login.model.LoginModel
import com.ezymd.restaurantapp.utils.*
import com.ezymd.restaurantapp.utils.JSONKeys.IS_MOBILE
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.header_new.*

/**
 * Created by Mohit on 19/10/2020.
 */
class OTPScreen : BaseActivity(), View.OnClickListener {
    private var otpReceiveListener: OTPReceiveListener? = null
    private var smsBroadcast: OtpBroadcastReceiver? = null
    private var isBackPressEnable = false
    private var otpViewModel: OtpViewModel? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        otpViewModel = ViewModelProvider(this).get(OtpViewModel::class.java)
        smsBroadcast = OtpBroadcastReceiver()
        registerReceiver()
        otpViewModel!!.startSmsListener(SmsRetriever.getClient(this))
        setOtpListener()
        setGui()
    }

    private fun unregisterReceiver() {
        this.unregisterReceiver(smsBroadcast)
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        this.registerReceiver(smsBroadcast, intentFilter)
    }

    private fun setOtpListener() {
        otpReceiveListener = object : OTPReceiveListener {
            override fun onOTPReceived(otp: String) {
                SnapLog.print("onOTPReceived=======$otp")
                if (otp.length == 4) {
                    icon1.setText(otp.substring(0, 1))
                    icon2.setText(otp.substring(1, 2))
                    icon3.setText(otp.substring(2, 3))
                    icon4.setText(otp.substring(3, 4))
                }
            }

            override fun onOTPTimeOut() {}
        }
        smsBroadcast!!.initOTPListener(otpReceiveListener!!)
    }

    fun onBack() {
        isBackPressEnable = true
        onBackPressed()
    }


    /*when back pressed activity does not close app goes in background*/
    /*override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (isBackPressEnable) moveTaskToBack(true)
                return true
            }
        }
        return false
    }*/

    private fun setGui() {
        icon1.requestFocus()
        otpViewModel!!.isLoading.observe(this, Observer {
            if (it) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
                progressLogin.visibility = View.GONE
            }
        })

        otpViewModel!!.otpSend.observe(this, Observer {
            ShowDialog(this).disPlayDialog(
                it.message,
                it.isStatus == ErrorCodes.SUCCESS,
                false
            )

            if (it.status == ErrorCodes.SUCCESS) {
                intent.putExtra(JSONKeys.OTP, it.data.otp)
            }


        })

        otpViewModel!!.loginResponse.observe(this, Observer {

            if (it.status == ErrorCodes.SUCCESS) {
                setLoginUser(it)

            } else {
                setErrorWithVibrator()
                showError(false, it.message, null)
            }
        })

        leftIcon.setOnClickListener(this)
        resendOtp.setOnClickListener(this)
        next.setOnClickListener(this)
        val spann = SpannableString(getString(R.string.did_not_receive_otp))
        val spannResend = SpannableString(getString(R.string.resend))
        spannResend.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_002366)),
            0,
            spannResend.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        resendOtp.text = TextUtils.concat(spann, " ", spannResend)

        val spannMsg = SpannableString(getString(R.string.enter_four_digit_code))
        val mobileNo = intent.getStringExtra(JSONKeys.MOBILE_NO)
        var mask = mobileNo.replace("\\w(?=\\w{4})".toRegex(), "x")
        mask = mask.chunked(3).joinToString(separator = "-")
        val spannMobile = SpannableString(mask)
        spannMobile.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.color_010a1d
                )
            ), 0, spannMobile.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        msg.text = TextUtils.concat(spannMsg, " ", spannMobile)


        setListenerOnEditText()
    }

    /*set listener on edit text*/
    private fun setListenerOnEditText() {
        icon4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })
        icon1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length == 1) {
                    icon2.requestFocus()
                }
            }
        })
        icon2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length == 1) {
                    icon3.requestFocus()
                }
            }
        })
        icon3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length == 1) {
                    icon4.requestFocus()
                }
            }
        })
    }


    /*set ui for wrong otp*/
    private fun setErrorWithVibrator() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500L)
        val animation = AnimationUtils.loadAnimation(this, R.anim.horizontal_shake)
        iconLay.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                clearOtp()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.leftIcon -> {
                v.alpha = 0.5f
                onBackPressed()
            }
            R.id.resendOtp -> resendSameOtp(v)
            R.id.next -> checkConditions(v)
        }
    }

    private fun checkConditions(v: View) {
        UIUtil.clickHandled(v)
        if (icon1.text.toString().trim().isEmpty()) {
            icon1.requestFocus()
        } else if (icon2.text.toString().trim().isEmpty()) {
            icon2.requestFocus()
        } else if (icon3.text.toString().trim().isEmpty()) {
            icon3.requestFocus()
        } else if (icon4.text.toString().trim().isEmpty()) {
            icon4.requestFocus()
        } else {
            SuspendKeyPad.suspendKeyPad(this)
            if (intent.hasExtra(IS_MOBILE)) {
                val otp = intent.getStringExtra(JSONKeys.OTP)
                val otpString = icon1.text.toString().trim() + icon2.text.toString()
                    .trim() + icon3.text.toString().trim() + icon4.text.toString().trim()
                if (otp!!.equals(otpString)) {
                    progressLogin.visibility = View.VISIBLE
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    setErrorWithVibrator()
                }
            } else {
                progressLogin.visibility = View.VISIBLE
                val loginRequest = LoginRequest()
                loginRequest.mobileNo = intent.getStringExtra(JSONKeys.MOBILE_NO)
                loginRequest.otp =
                    icon1.text.toString() + icon2.text.toString() + icon3.text.toString() + icon4.text.toString()
                otpViewModel?.registerUser(loginRequest)
            }
        }

    }

    /*resend sms or otp*/
    private fun resendSameOtp(v: View) {
        UIUtil.clickAlpha(v)
        otpViewModel!!.startLoading(true)
        otpViewModel!!.resendOtp(
            intent.getStringExtra(JSONKeys.MOBILE_NO)!!,intent.getStringExtra(JSONKeys.COUNTRY_CODE)!!,
            intent.hasExtra(IS_MOBILE)
        )

    }

    override fun onDestroy() {
        if (otpReceiveListener != null) {
            otpReceiveListener = null
        }
        unregisterReceiver()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    /*clear otp when wrong is entered*/
    fun clearOtp() {
        icon1.setText("")
        icon2.setText("")
        icon3.setText("")
        icon4.setText("")
        icon1.requestFocus()
    }

    private fun setLoginUser(it: LoginModel) {
        setResult(Activity.RESULT_OK)
        userInfo?.accessToken = it.data.access_token
        userInfo?.userName = it.data.user.name
        userInfo?.email = it.data.user.email
        userInfo?.userID = it.data.user.id
        userInfo?.phoneNumber = it.data.user.phone_no
        userInfo?.profilePic = it.data.user.profile_pic
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }

}