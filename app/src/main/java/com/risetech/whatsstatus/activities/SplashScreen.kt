package com.risetech.whatsstatus.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.risetech.whatsstatus.R
import java.util.*


class SplashScreen : AppCompatActivity() {

    //lateinit var imgWelcome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        object : CountDownTimer(500, 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                nextActivity()
            }

        }.start()

        /*try {
            setContentView(R.layout.activity_splash_screen)
        } catch (ex: Exception) {
            nextActivity()
            return
        }

        try {

            imgWelcome = findViewById(R.id.imgWelcome)

            val myAnim = AnimationUtils.loadAnimation(this@SplashScreen, R.anim.zoom_splash)
            imgWelcome.startAnimation(myAnim)

            val constraintLayout = findViewById<ConstraintLayout>(R.id.layout)
            val animationDrawable = constraintLayout.background as AnimationDrawable
            animationDrawable.setEnterFadeDuration(500)
            animationDrawable.setExitFadeDuration(1000)
            animationDrawable.start()

            object : CountDownTimer(5000, 1000) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    nextActivity()
                }

            }.start()

        } catch (ex: java.lang.Exception) {
            nextActivity()
        }*/

    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun nextActivity() {
        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        finish()
    }

}