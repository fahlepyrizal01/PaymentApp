package com.example.paymentapp.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.paymentapp.MainActivity
import com.example.paymentapp.R
import com.example.paymentapp.res.SharedPrefManager



class SplashScreenActivity : AppCompatActivity() {

    lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initiationWidget()
    }

    private fun initiationWidget(){
        context = this@SplashScreenActivity

        Handler().postDelayed({
            loadSession()
        }, 2000)
    }

    private fun loadSession(){
        if (SharedPrefManager.getLoggedStatus(applicationContext)) {
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }else {
            startActivity(Intent(context, LoginActivity::class.java))
            finish()
        }
    }
}
