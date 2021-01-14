package com.risetech.statussaver.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.risetech.statussaver.BuildConfig

object AdManger {

    var mIntersital: InterstitialAd? = null
    private lateinit var mContext: Context
    var adCallbackInterstisial: AdManagerListener? = null

    //Interstitial Ads
    val interstitialId = "ca-app-pub-5448910982838601/5807912603"
    val interstitialTestId = "ca-app-pub-3940256099942544/1033173712"

    @JvmStatic
    fun init(context: Context) {
        mContext = context
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context)
        mIntersital = InterstitialAd(context)

        if (BuildConfig.DEBUG) {
            Log.d("AdManager", "new load request with Debug")
            mIntersital?.adUnitId = interstitialTestId
        } else {
            Log.d("AdManager", "new load request with release")
            mIntersital?.adUnitId = interstitialId
        }
    }

    @JvmStatic
    fun loadInterstial(context: Context, adCallback: AdManagerListener) {
        adCallbackInterstisial = adCallback
        if (mIntersital == null) {
            init(context)
        } else {
            try {
                mIntersital?.let {
                    it.loadAd(AdRequest.Builder().build())
                    adListner()
                }
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
                init(context)
            }
        }
    }

    @JvmStatic
    private fun adListner() {

        mIntersital?.let {

            it.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    Log.d("AdManager", "Ad Loaded")
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    Log.d("AdManager", "Ad Failed To Load $errorCode")
                }

                override fun onAdOpened() {
                    Log.d("AdManager", "Ad Opened")
                }

                override fun onAdClicked() {
                    Log.d("AdManager", "Ad Clicked")
                }

                override fun onAdLeftApplication() {
                    Log.d("AdManager", "User Left Application")
                }

                override fun onAdClosed() {
                    Log.d("AdManager", "Ad Closed")
                    if (adCallbackInterstisial != null) {
                        loadInterstial(mContext, adCallbackInterstisial!!)

                        adCallbackInterstisial?.onAdClose()
                    }
                }

            }
        }
    }

    @JvmStatic
    fun showInterstial(context: Context) {
        if (mIntersital == null) {
            init(context)
        } else {
            mIntersital!!.show()
        }
    }

    @JvmStatic
    fun isInterstialLoaded(context: Context): Boolean {
        var isLoad = false
        if (mIntersital == null) {
            init(context)
        } else {
            isLoad = mIntersital!!.isLoaded
        }
        return isLoad
    }

    interface AdManagerListener {
        fun onAdClose()
    }

}