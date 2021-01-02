package com.risetech.whatsstatus.ads


import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.*
import com.risetech.whatsstatus.BuildConfig
import com.risetech.whatsstatus.utils.Constants
import com.risetech.whatsstatus.utils.Utils

object AdManger {

    lateinit var mIntersital: InterstitialAd
    var adCallbackInterstisial: AdManagerListener? = null
    lateinit var mActivity: Activity
    var TAG: String = "myAds"

    @JvmStatic
    fun init(activity: Activity) {
        this.mActivity = activity

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(activity) {}
        mIntersital = InterstitialAd(activity)

        if (BuildConfig.DEBUG) {
            mIntersital.adUnitId = Constants.interstitialTestId
        } else {
            mIntersital.adUnitId = Constants.interstitialId
        }

    }

    @JvmStatic
    fun loadIntersital(adCallback: AdManagerListener?) {

        adCallbackInterstisial = adCallback

        if (!mIntersital.isLoaded) {

            try {
                if (Utils.isNetworkAvailable(mActivity)) {
                    mIntersital.loadAd(AdRequest.Builder().build())
                    adListenerInterface()
                    Log.e(TAG, "Ads is load")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun showInterstial() {
        if (mIntersital.isLoaded) {
            mIntersital.show()
            Log.e(TAG, "Ads is show")
        }

        /*this.mIntersital.adListener = object : AdListener() {
            override fun onAdClosed() {
                try {
                    adCallbackInterstisial?.onAdCloseActivity()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }*/

    }

    private fun adListenerInterface() {

        mIntersital.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                @Suppress("DEPRECATION")
                super.onAdFailedToLoad(p0)
                //Error code 0 not internet connect
                Log.e("AdManager", "ad load failed , $p0")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e("AdManager", "ad successfully loaded")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.e("AdManager", "onAdImpression")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.e("AdManager", "onAdClosed")
                adCallbackInterstisial?.onAdCloseActivity()
            }
        }

    }

    fun isInterstialLoaded(): Boolean {
        return mIntersital.isLoaded
    }

    interface AdManagerListener {
        fun onAdCloseActivity()
    }

}