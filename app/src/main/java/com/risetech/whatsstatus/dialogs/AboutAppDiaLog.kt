package com.risetech.whatsstatus.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.risetech.whatsstatus.R
import java.util.*

@Suppress("UNNECESSARY_SAFE_CALL")
class AboutAppDiaLog(private val activity: Activity) {

    private fun setDialog() {

        //*****************************Create a custom Dialog***************************************//
        val dialog = Dialog(activity, R.style.full_screen_dialog)
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view =
            inflater.inflate(R.layout.about_dialog, null, false)
        Objects.requireNonNull(dialog.window)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Objects.requireNonNull(dialog.window)
            ?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.window!!.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        dialog.setContentView(view)

        val backBtn = view.findViewById<ImageView>(R.id.imageView261)
        val textView: TextView = view.findViewById(R.id.textView360)
        var infoString = ""

        infoString = textView.text.toString().trim()

        try {

            val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            val version = pInfo.versionName
            infoString += " " + version
            textView.text = infoString

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        backBtn.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    init {
        setDialog()
    }

}