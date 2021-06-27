package com.ezymd.restaurantapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ezymd.restaurantapp.utils.SnapLog
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class OtpBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null

    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    SnapLog.print("OTP_Message" + otp)
                    if (this.otpReceiver != null) {
                        otp = parseCode(otp)
                        SnapLog.print("OTP_Message" + otp)
                        SnapLog.print(otp)
                        this.otpReceiver!!.onOTPReceived(otp)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {

                }


            }
        }
    }

    fun parseCode( message:String):String {
        val p = Pattern.compile("\\b\\d{4}\\b");
        val m = p.matcher(message);
        var code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }

}