package com.risetech.statussaver.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.risetech.statussaver.R
import java.util.*

class SplashScreen : AppCompatActivity() {

    //lateinit var imgWelcome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //AdManger.init(this@SplashScreen)

        object : CountDownTimer(500, 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                nextActivity()
            }

        }.start()

    }


    private fun nextActivity() {
        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        finish()
    }

}