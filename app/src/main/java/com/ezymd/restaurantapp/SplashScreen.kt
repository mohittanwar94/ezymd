package com.ezymd.restaurantapp

import android.content.Intent
import android.os.Bundle
import com.ezymd.restaurantapp.login.Login

class SplashScreen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (userInfo!!.userID != 0)
            startActivity(Intent(this, MainActivity::class.java))
        else
            startActivity(Intent(this, Login::class.java))

        overridePendingTransition(R.anim.left_in, R.anim.left_out)
        this.finish()
    }
}