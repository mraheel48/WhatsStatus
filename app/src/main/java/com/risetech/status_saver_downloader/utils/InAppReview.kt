package com.risetech.status_saver_downloader.utils

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory

//implementation 'com.google.android.play:core:1.9.0'

object InAppReview {

    var reView = false

    @JvmStatic
    fun startInAppReview(activity: Activity) {

        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener {

            if (it.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = it.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)

                flow.addOnCompleteListener { task ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.

                    if (task.isSuccessful) {
                        Log.e("myInAppReview", "successful")
                    } else {
                        Log.e("myInAppReview", "not successful launch")
                    }
                }

            } else {
                Log.e("myInAppReview", " is Error")
                // There was some problem, continue regardless of the result.
            }
        }
    }

    @JvmStatic
    fun startInAppReviewWith(activity: Activity): Boolean {

        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener {

            if (it.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = it.result

                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { task ->

                    reView = if (task.isSuccessful) {

                        Log.e("myInAppReview", "successful")
                        true

                    } else {

                        Log.e("myInAppReview", "not successful launch")
                        false
                    }

                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }


            } else {
                Log.e("myInAppReview", " is Error")
                reView = false
                // There was some problem, continue regardless of the result.
            }
        }
        return reView
    }

}