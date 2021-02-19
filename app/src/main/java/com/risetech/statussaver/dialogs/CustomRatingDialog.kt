package com.risetech.statussaver.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import com.risetech.statussaver.R
import com.risetech.statussaver.utils.Utils
import java.lang.Exception
import java.util.*

@Suppress("UNNECESSARY_SAFE_CALL")
class CustomRatingDialog(private val activity: Activity) {

    private fun setDialog() {

        //*****************************Create a custom Dialog***************************************//
        val customDialog = AlertDialog.Builder(activity).create()
        val layoutInflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.rating_view, null)
        customDialog.setView(view)
        Objects.requireNonNull(customDialog.window)

            ?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnCancel = view.findViewById<TextView>(R.id.btnClose)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _: RatingBar?, rating: Float, _: Boolean ->

                if (rating >= 1) {

                    customDialog.dismiss()
                    try {
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id=" + "com.risetech.status.downloader.saver.story")
                            )
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                } else {
                    customDialog.dismiss()
                }
            }

        btnCancel.setOnClickListener {
            customDialog.dismiss()
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri
                            .parse("market://details?id=" + "com.risetech.status.downloader.saver.story")
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        customDialog.show()
        // customDialog.setCancelable(false)
    }

    init {
        setDialog()
    }

}