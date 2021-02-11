package com.risetech.statussaver.ads


import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.risetech.statussaver.BuildConfig

object AdManager {

    var mIntersital: InterstitialAd? = null

    private lateinit var mContext: Context
    var adCallbackInterstisial: CallbackInterstial? = null

    //Note! this id must b change not a Simple id
    private const val interstitialAdsSimple = "ca-app-pub-3940256099942544/1033173712"
    private const val interstitialAds = "ca-app-pub-5448910982838601/5807912603"

    private const val TAG = "AdManager"

    @JvmStatic
    fun init(context: Context) {

        mContext = context
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context)
        mIntersital = InterstitialAd(context)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "ads with Debug")
            mIntersital?.adUnitId = interstitialAdsSimple
        } else {
            Log.d(TAG, "ads with release")
            mIntersital?.adUnitId = interstitialAds
        }
    }

    fun loadInterstial(context: Context, adCallback: CallbackInterstial) {
        adCallbackInterstisial = adCallback
        try {
            mIntersital?.let {

                if (!it.isLoaded) {
                    it.loadAd(AdRequest.Builder().build())
                    adListner()
                    Log.d(TAG, "Ads is not load")
                } else {
                    Log.d(TAG, "Ads is already load")
                }
            }
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
            init(context)
        }

    }

    private fun adListner() {

        mIntersital?.let {

            it.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    Log.d("AdManager", "Ad Loaded")
                    adCallbackInterstisial?.onAdLoaded()
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    if (errorCode == AdRequest.ERROR_CODE_NO_FILL) {
                        Log.d("AdManager", "Ad Failed To Load = Ad request successful, but no ad returned due to lack of ad inventory.")
                    }
                    adCallbackInterstisial?.onAdFailedToLoad(errorCode)
                }

                override fun onAdOpened() {
                    Log.d("AdManager", "Ad Opened")
                    adCallbackInterstisial?.onAdOpened()
                }

                override fun onAdClicked() {
                    Log.d("AdManager", "Ad Clicked")
                    adCallbackInterstisial?.onAdClicked()
                }

                override fun onAdLeftApplication() {
                    Log.d("AdManager", "User Left Application")
                    adCallbackInterstisial?.onAdLeftApplication()
                }

                override fun onAdClosed() {
                    Log.d("AdManager", "Ad Closed")
                    loadInterstial(mContext, adCallbackInterstisial!!)
                    adCallbackInterstisial?.onAdClosed()
                }

            }
        }
    }

    interface CallbackInterstial {
        fun onAdLoaded()
        fun onAdFailedToLoad(errorCode: Int)
        fun onAdOpened()
        fun onAdClicked()
        fun onAdLeftApplication()
        fun onAdClosed()
    }

    fun showInterstial(context: Context, adCallback: CallbackInterstial) {

        if (mIntersital != null) {
            if (mIntersital!!.isLoaded) {
                adCallbackInterstisial = adCallback
                mIntersital?.show()
                loadInterstial(context, adCallback)
            } else {
                loadInterstial(context, adCallback)
            }

        } else {
            init(context)
        }

    }

    fun isInterstialLoaded(): Boolean {
        return mIntersital!!.isLoaded
    }

}