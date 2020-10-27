package com.ezymd.restaurantapp

/**
 * Created by VTSTN-27 on 8/17/2016.
 */
interface ServerConfig {
    companion object {

        const val IS_TESTING = true
        const val facebook = "JsmOX78jPApjOOP7ozZxtZL/Yto="
        const val BASE_URL = "http://15.207.72.214/ezymd/api/"

        const val OTP_HASH = "FCWFd3gBNu0"


        const val GENERATE_OTP = "sendOtp"
        const val SOCIAL_LOGIN_USER = "socialLoginRegister"
        const val LOGIN_USER = "loginRegister"
    }
}
