package com.risetech.whatsstatus.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.utils.Utils
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

                if (rating > 3) {

                    Utils.showToast(activity, "Working rating Dialog")
                    customDialog.dismiss()
                    /*customDialog.dismiss()

                    val it = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + mContext.packageName)
                    )

                    mContext.startActivity(it)
                    */
                } else customDialog.dismiss()
            }
        btnCancel.setOnClickListener { customDialog.dismiss() }
        customDialog.show()
        // customDialog.setCancelable(false)
    }

    init {
        setDialog()
    }

}