package com.ezymd.restaurantapp

/**
 * Created by VTSTN-27 on 8/17/2016.
 */
interface ServerConfig {
    companion object {


        const val PAYMENT_ACCOUNT_ID = "acct_1HhzYpJDOhLt3uxI"
        const val PAYMENT_PUBLISHABLE_KEY =
            "pk_test_51HhzYpJDOhLt3uxIvDleCx6AuZhRyxcbqjlcDDpT0T5eJpcsn75VDkvIwFBPGrZbcqMgOpYcW8orGH8e38SmimGC00isW5kwyM"
        const val IS_TESTING = true
        const val facebook = "JsmOX78jPApjOOP7ozZxtZL/Yto="
        const val BASE_URL = "http://34.121.92.176/ezymd/public/api/"

        const val OTP_HASH = "FCWFd3gBNu0"


        const val GENERATE_OTP = "sendOtp"
        const val LIST_BANNER = "nearByRestaurantBanner"
        const val LOGOUT = "logout"
        const val LIST_RESTURANTS = "nearByRestaurant"
        const val SEARCH_RESTURANTS = "nearByRestaurant"
        const val RESTURANT_DETAILS = "restaurantItems/{id}"
        const val RESTURANT_FILTERS = "getFilters"
        const val SOCIAL_LOGIN_USER = "socialLoginRegister"
        const val DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json"
        const val LOGIN_USER = "loginRegister"
        const val LIST_TRENDING = "trendingFoods"
        const val CREATE_ORDER = "order"
        const val CREATE_CUSTOMER="stripe/createCustomer"
        const val EPHEMERAL_KEYS="stripe/ephemeralKey"
        const val LOCATION_VALIDATE="validateLocations"
        const val SAVE_PAYMENT="paymentVerify"
        const val SAVE_RATING="addReview"
        const val APPLY_COUPON="applyCoupon"
        const val LIST_COUPON="listCoupon/{restaurant_id}"
        const val TRANSACTION_CHARGES="transactionAmount"
        const val UPDATED_COORDINATES="orderTracking/{name}"

        val ZENDESK_CHAT_KEY = "X40lrThEJB7Lgs6XSUGf9kVbzDjbWNQa";
        val APPID = "220869949208236033"


    }
}
