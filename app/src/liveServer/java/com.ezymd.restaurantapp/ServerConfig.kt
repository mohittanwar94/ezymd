package com.ezymd.restaurantapp

/**
 * Created by VTSTN-27 on 8/17/2016.
 */
interface ServerConfig {
    companion object {


        const val IS_TESTING = false
        const val facebook = "JsmOX78jPApjOOP7ozZxtZL/Yto="
        const val BASE_URL = "https://app.ezymd.com/api/"

        const val OTP_HASH = "wGcYmfGND6w"

        const val APP_MD5 = "d2a57dc1d883fd21fb9951699df71cc7"
        const val EZYMD_MD5 = "3792e262ce5bc06d7a1f4fe1c68f5cf5"
        const val GENERATE_OTP = "sendOtp"
        const val LOGOUT = "logout"

        /*const val LIST_RESTURANTS = "nearByRestaurant"
        const val SEARCH_RESTURANTS = "nearByRestaurant"*/
        const val RESTURANT_DETAILS = "restaurantItems/{id}"
        const val RESTURANT_FILTERS = "getFilters"
        const val SOCIAL_LOGIN_USER = "socialLoginRegister"
        const val DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json"
        const val LOGIN_USER = "loginRegister"
        const val CREATE_ORDER = "order"
        const val CREATE_CUSTOMER = "stripe/createCustomer"
        const val EPHEMERAL_KEYS = "stripe/ephemeralKey"
        const val LOCATION_VALIDATE = "validateLocations"
        const val SAVE_PAYMENT = "paymentVerify"
        const val SAVE_RATING = "addReview"
        const val SHOP_DETAIL = "shopDetail"
        const val SHOP_PRODUCT_DETAIL = "shopCategoryProducts"
        const val APPLY_COUPON = "applyCoupon"
        const val LIST_COUPON = "listCoupon/{restaurant_id}"
        const val PRODUCT_DETAIL = "productDetail"
        const val GET_SHOP_REVIEW = "getShopReview"
        const val TRANSACTION_CHARGES = "transactionAmount"
        const val UPDATED_COORDINATES = "orderTracking/{name}"

        val ZENDESK_CHAT_KEY = "";
        val APPID = ""

        const val ACCEPT_ORDER = "updateOrderStatus"
        const val COD_ORDER = "codOrder"

        const val TRENDING_STORES = "trendingItems"
        const val NEAR_BY_SHOPS = "nearByShops"
        const val NEAR_BY_BANNERS = "nearByShopBanner"
        const val UPDATE_PROFILE = "updateProfile"
        const val FAQ_URL = "https://www.ezymd.com/faq"
        const val REFERRAL_DETAILS = "referralCode"
        const val SAVE_REFERRAL = "useReferralCode"
        const val WALLET_BALANCE = "walletTransactions"
        const val EMAIL_INVOICE = "sendInvoice"
        const val CONFIG = "config"


        /* TO Do change to this to near by restauant */
        //const val LIST_BANNER = "nearByRestaurantBanner"
        // const val LIST_TRENDING = "trendingFoods"


    }
}
