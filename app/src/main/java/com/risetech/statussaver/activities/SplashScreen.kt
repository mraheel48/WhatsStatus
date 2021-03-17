package com.risetech.statussaver.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.risetech.statussaver.R
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_splash_screen)
            object : CountDownTimer(500, 1) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    nextActivity()
                }

            }.start()

        } catch (ex: Exception) {
            ex.printStackTrace()
            nextActivity()
        }

    }


    private fun nextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}