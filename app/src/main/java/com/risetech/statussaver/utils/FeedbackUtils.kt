package com.risetech.statussaver.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.risetech.statussaver.R
import java.util.*

object FeedbackUtils {

    private fun getDeviceInfo(context: Context): String {

        val sdk = Build.VERSION.SDK_INT      // API Level
        val model = Build.MODEL            // Model
        val brand = Build.BRAND          // Product

        var infoString = ""
        val locale: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).country
        } else {
            Locale.getDefault().country.toLowerCase(Locale.ROOT)
        }
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            infoString += "Application Version: $version\n"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        infoString += "Brand: " + brand + " (" + model + ")\n" +
                "Android API: " + sdk

        if (locale.isNotEmpty()) {
            infoString += "\nCountry: $locale"
        }
        return infoString
    }

    private fun emailSubject(context: Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val version = pInfo.versionName
            val name = "Whats Status"//getApplicationName(context)
            return "$name - $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return context.getString(R.string.app_name)
    }

    @JvmStatic
    fun startFeedbackEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("risetech69@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject(context))
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "\n\n--Please write your question above this--\n${getDeviceInfo(context)}"
        )

        context.startActivity(Intent.createChooser(intent, "Email via..."))
    }

}