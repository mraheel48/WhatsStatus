package com.risetech.whatsstatus.ads


import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.risetech.whatsstatus.BuildConfig
import com.risetech.whatsstatus.utils.Constants
import com.risetech.whatsstatus.utils.Utils


object AdManger {

    lateinit var mIntersital: InterstitialAd
    var adCallbackInterstisial: AdManagerListener? = null
    lateinit var context: Context
    var TAG: String = "myAds"

    @JvmStatic
    fun init(activity: Activity) {

        MobileAds.initialize(activity)

        mIntersital = InterstitialAd(activity)

        if (BuildConfig.DEBUG) {
            mIntersital.adUnitId = Constants.interstitialTestId
        } else {
            mIntersital.adUnitId = Constants.interstitialId
        }

        this.context = activity
    }

    @JvmStatic
    fun loadIntersital(adCallback: AdManagerListener?) {

        adCallbackInterstisial = adCallback

        if (!mIntersital.isLoaded) {

            try {

                if (Utils.isNetworkAvailable(context)) {
                    mIntersital.loadAd(AdRequest.Builder().build())
                    Log.e(TAG, "Ads is load")
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

    }

    @JvmStatic
    fun showInterstial(adCallback: AdManagerListener?) {

        adCallbackInterstisial = adCallback

        if (mIntersital.isLoaded) {
            mIntersital.show()
            Log.e(TAG, "Ads is show")
        }

        this.mIntersital.adListener = object : AdListener() {

            override fun onAdClosed() {

                try {
                    adCallbackInterstisial?.onAdCloseActivity()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

    }

    fun isInterstialLoaded(): Boolean {
        return mIntersital.isLoaded
    }

    interface AdManagerListener {
        fun onAdClose(dataCopy: String, idPosition: Int)
        fun onAdClose(pos: Int)
        fun onAdCloseActivity()
    }

}