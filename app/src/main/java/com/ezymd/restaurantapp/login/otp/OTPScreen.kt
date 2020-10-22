package com.ezymd.restaurantapp.login.otp

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Vibrator
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
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
import com.ezymd.restaurantapp.utils.JSONKeys
import com.ezymd.restaurantapp.utils.ShowDialog
import com.ezymd.restaurantapp.utils.SnapLog
import com.ezymd.restaurantapp.utils.UIUtil
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.header_new.*

/**
 * Created by Mohit on 19/10/2020.
 */
class OTPScreen : BaseActivity(), View.OnClickListener {
    private var otp = ""
    private val server_otp = ""
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

    fun onStartHomeScreen() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    /*when back pressed activity does not close app goes in background*/
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (isBackPressEnable) moveTaskToBack(true)
                return true
            }
        }
        return false
    }

    private fun setGui() {
        otpViewModel!!.isLoading.observe(this, Observer {
            progress.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        otpViewModel!!.otpSend.observe(this, Observer {
            ShowDialog(this).disPlayDialog(it.msg, it.isSended, false)
        })

        leftIcon.setOnClickListener(this)
        resendOtp.setOnClickListener(this)
        val spann = SpannableString(getString(R.string.did_not_receive_otp))
        val spannResend = SpannableString(getString(R.string.resend))
        spannResend.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_333)),
            0,
            spannResend.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        resendOtp.text = TextUtils.concat(spann, " ", spannResend)

        val spannMsg = SpannableString(getString(R.string.enter_four_digit_code))
        val spannMobile = SpannableString(intent.getStringExtra(JSONKeys.MOBILE_NO))
        spannMobile.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.bookmark_inactive_clr
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
                if (s.length == 1) {
                    otp = icon1.text.toString().trim() + icon2.text.toString()
                        .trim() + icon3.text.toString().trim() + icon4.text.toString()
                        .trim()
                    gotoConditionCheck(otp)
                }
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

    /*check condition otp match or not*/
    private fun gotoConditionCheck(otp: String) {
        if (otp == server_otp) {
        } else {
            setErrorWithVibrator()
        }
    }

    /*set ui for wrong otp*/
    private fun setErrorWithVibrator() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
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
            R.id.leftIcon -> if (isBackPressEnable) {
                v.alpha = 0.5f
                onBackPressed()
            }
            R.id.resendOtp -> resendSameOtp(v)
        }
    }

    /*resend sms or otp*/
    private fun resendSameOtp(v: View) {
        UIUtil.clickAlpha(v)
        otpViewModel!!.startLoading(true)
        otpViewModel!!.resendOtp(intent.getStringExtra(JSONKeys.MOBILE_NO)!!)

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
        otp = ""
        icon1.setText("")
        icon2.setText("")
        icon3.setText("")
        icon4.setText("")
        icon1.requestFocus()
    }


}