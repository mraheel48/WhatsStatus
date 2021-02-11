package com.risetech.statussaver.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.Purchase.PurchaseState.PURCHASED

class GoogleBilling(var activity: Activity, var context: Context, var billingHandler: GoogleBillingHandler) {
    private var billingClient: BillingClient
    var isConnected = false
    private var purchaseType = ""
    private var savedProductId = ""

    companion object {
        @JvmStatic
        var isSubscriptionCached = false

        @JvmStatic
        var isInAppCached = false

        @JvmStatic
        private var cachedSubscriptionStatusList: ArrayList<String> = ArrayList()

        @JvmStatic
        private var cachedInAppStatusList: ArrayList<String> = ArrayList()

        @JvmStatic
        fun getPriceValueFromMicros(value: Long): Double {
            return value.toDouble() / 1000000.0
        }

        private const val CALL_BACK = "CALL_BACK"
        private const val NO_CALL_BACK = "NO_CALL_BACK"
    }

    object ResponseCodes {
        const val SERVICE_TIMEOUT = -3
        const val FEATURE_NOT_SUPPORTED = -2
        const val SERVICE_DISCONNECTED = -1
        const val OK = 0
        const val USER_CANCELED = 1
        const val SERVICE_UNAVAILABLE = 2
        const val BILLING_UNAVAILABLE = 3
        const val ITEM_UNAVAILABLE = 4
        const val DEVELOPER_ERROR = 5
        const val ERROR = 6
        const val ITEM_ALREADY_OWNED = 7
        const val ITEM_NOT_OWNED = 8
        const val NOT_PURCHASED_STATE = 109
        const val NOT_CONNECTED = 110
    }

    init {
        val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach { itPurchase ->
                    if (itPurchase.sku == savedProductId) {
                        Log.e("SubscriptionTest", " PurchaseUpdatedListener Called")
                        acknowledgePurchase(itPurchase, CALL_BACK)
                    }
                }
            } else {
                billingHandler.onBillingError(billingResult.responseCode)
            }
        }
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build()
    }

    fun isSubscribedOrPurchase(subscriptionList: ArrayList<String>?, inAppList: ArrayList<String>?): Boolean {
        var subResult = false
        var inAppResult = false
        subscriptionList?.let { itList ->
            subResult = isSubscribedAny(itList)
            Log.e("SubscriptionTest", "Sub $subResult")
        }
        inAppList?.let { itList ->
            inAppResult = isPurchasedAny(itList)
            Log.e("SubscriptionTest", "InApp $inAppResult")
        }
        return subResult || inAppResult
    }

    fun isSubscribed(productId: String): Boolean {
        return if (isSubscriptionCached) {
            Log.e("SubscriptionTest", "Cached Result $cachedSubscriptionStatusList")
            cachedSubscriptionStatusList.contains(productId)
        } else {
            val result = isSubscribedRealtime(productId)
            Log.e("SubscriptionTest", "Not Cached Result $result")
            result
        }
    }

    fun isPurchased(productId: String): Boolean {
        return if (isInAppCached) {
            Log.e("SubscriptionTest", "Cached")
            cachedInAppStatusList.contains(productId)
        } else {
            Log.e("SubscriptionTest", "Not Cached")
            isPurchasedRealtime(productId)
        }
    }

    fun isSubscribedAny(productIdList: ArrayList<String>): Boolean {
        if (isSubscriptionCached) {
            productIdList.forEach { itProductId ->
                Log.e("SubscriptionTest", "Cached Any Result  $cachedSubscriptionStatusList  $ ")
                if (cachedSubscriptionStatusList.contains(itProductId)) {
                    return true
                }
            }
            return false
        } else {
            val a = isSubscribedAnyRealtime(productIdList)
            Log.e("SubscriptionTest", "Not Cached Any Result $a")
            return a
        }
    }

    fun isPurchasedAny(productIdList: ArrayList<String>): Boolean {
        if (isInAppCached) {
            Log.e("SubscriptionTest", "Cached")
            productIdList.forEach { itProductId ->
                if (cachedInAppStatusList.contains(itProductId)) {
                    return true
                }
            }
            return false
        } else {
            Log.e("SubscriptionTest", "Not Cached")
            return isPurchasedAnyRealtime(productIdList)
        }
    }

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    if (isInAppCached && isSubscriptionCached) {
                        isConnected = true
                        billingHandler.onBillingInitialized()
                    } else {
                        if (!isInAppCached) {
                            reloadInAppCache()
                        }
                        if (!isSubscriptionCached) {
                            reloadSubscriptionCache()
                        }
                        if (isInAppCached && isInAppCached) {
                            isConnected = true
                            billingHandler.onBillingInitialized()
                        }
                    }

                } else {
                    billingHandler.onBillingError(billingResult.responseCode)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                isConnected = false
                billingHandler.onBillingServiceDisconnected()
            }
        })
    }

    fun getSubscriptionsSkuDetails(productIdList: ArrayList<String>, callback: (error: Int?, skuList: List<SkuDetails>?) -> Unit) {
        if (isConnected) {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(productIdList).setType(SkuType.SUBS)
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    callback(null, skuDetailsList)
                } else {
                    callback(billingResult.responseCode, skuDetailsList)
                    billingHandler.onBillingError(billingResult.responseCode)
                }
            }
        } else {
            callback(110, null)
        }
    }

    fun getInAppSkuDetails(productIdList: ArrayList<String>, callback: (error: Int?, skuList: List<SkuDetails>?) -> Unit) {
        if (isConnected) {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(productIdList).setType(SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    callback(null, skuDetailsList)
                } else {
                    callback(billingResult.responseCode, skuDetailsList)
                    billingHandler.onBillingError(billingResult.responseCode)
                }
            }
        } else {
            callback(110, null)
        }
    }

    fun purchase(productId: String) {
        if (isConnected) {
            purchaseType = SkuType.INAPP
            savedProductId = productId
            var skuDetails: SkuDetails? = null
            getInAppSkuDetails(arrayListOf(productId)) { errorCode: Int?, skuList: List<SkuDetails>? ->
                if (errorCode == null) {
                    skuList?.forEach { itSkuDetail ->
                        if (itSkuDetail.sku == productId) {
                            skuDetails = itSkuDetail
                        }
                    }
                    skuDetails?.let {
                        val billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(it)
                                .build()
                        val response = billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
                        if (response != BillingClient.BillingResponseCode.OK) {
                            isInAppCached = false
                            billingHandler.onBillingError(response)
                        }
                    }
                } else {
                    billingHandler.onBillingError(errorCode)
                }
            }
        } else {
            Log.e("SubscriptionTest", "fun Purchase")
            billingHandler.onBillingError(110)
        }
    }

    fun isSubscribedRealtime(productId: String): Boolean {
        var check = false
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.SUBS)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    cachedSubscriptionStatusList.clear()
                    isSubscriptionCached = true
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId == itPurchase.sku && itPurchase.purchaseState == PURCHASED
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedSubscriptionStatusList.add(itPurchase.sku)
                            if (!itPurchase.isAcknowledged) {
                                acknowledgePurchase(itPurchase, NO_CALL_BACK)
                            }
                        }
                        Log.e("SubscriptionTest", productId + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString() + "skuSize" + cachedSubscriptionStatusList.size)
                    }
                    Log.e("SubscriptionTest", "skuDetails $cachedSubscriptionStatusList")
                }
            } else {
                Log.e("SubscriptionTest", "Billing Error:" + purchaseResult.responseCode.toString())
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun isSubscribedRealtime")
            billingHandler.onBillingError(110)
        }
        Log.e("SubscriptionTest", "Check Returned")
        return check
    }

    fun getSubscriptionPurchaseDetails(productId: String): Purchase? {
        var check = false
        var purchase1: Purchase? = null
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.SUBS)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    cachedSubscriptionStatusList.clear()
                    isSubscriptionCached = true
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId == itPurchase.sku && itPurchase.purchaseState == PURCHASED
                            if (check) {
                                purchase1 = itPurchase
                            }
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedSubscriptionStatusList.add(itPurchase.sku)
                        }
                        Log.e("SubscriptionTest", productId + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    }
                }
            } else {
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun getSubscriptionPurchaseDetails")
            billingHandler.onBillingError(110)
        }
        return purchase1
    }

    fun isPurchasedRealtime(productId: String): Boolean {
        var check = false
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.INAPP)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    cachedInAppStatusList.clear()
                    isInAppCached = true
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId == itPurchase.sku && itPurchase.purchaseState == PURCHASED
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedInAppStatusList.add(itPurchase.sku)
                        }
                        if (!itPurchase.isAcknowledged) {
                            acknowledgePurchase(itPurchase, NO_CALL_BACK)
                        }
                        Log.e("SubscriptionTest", productId + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    }
                }
            } else {
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun isPurchasedRealtime")
            billingHandler.onBillingError(110)
        }
        return check
    }

    fun getInAppPurchaseDetails(productId: String): Purchase? {
        var check = false
        var purchase1: Purchase? = null
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.INAPP)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    cachedInAppStatusList.clear()
                    isInAppCached = true
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId == itPurchase.sku && itPurchase.purchaseState == PURCHASED
                            if (check) {
                                purchase1 = itPurchase
                            }
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedInAppStatusList.add(itPurchase.sku)
                        }
                        Log.e("SubscriptionTest", productId + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    }
                }
            } else {
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun getInAppPurchaseDetails")
            billingHandler.onBillingError(110)
        }
        return purchase1
    }

    interface GoogleBillingHandler {
        fun onBillingInitialized()
        fun onPurchased(purchase: Purchase)
        fun onBillingServiceDisconnected()
        fun onBillingError(errorCode: Int)
    }

    private fun acknowledgePurchase(purchase: Purchase, type: String) {
        if (purchase.purchaseState == PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { itBillingResult ->
                    if (itBillingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.e("SubscriptionTest", "Acknowledged")
                        if (type == CALL_BACK) {
                            when (purchaseType) {
                                SkuType.INAPP -> {
                                    isInAppCached = false
                                }
                                SkuType.SUBS -> {
                                    isSubscriptionCached = false
                                }
                                else -> {
                                    isInAppCached = false
                                    isSubscriptionCached = false
                                }
                            }
                            billingHandler.onPurchased(purchase)
                        }
                    } else {
                        billingHandler.onBillingError(itBillingResult.responseCode)
                    }
                }
            } else {
                billingHandler.onPurchased(purchase)
            }
        } else {
            billingHandler.onBillingError(109)
        }
    }

    fun consumePurchase(productId: String, callback: (error: Int?, outToken: String?) -> Unit) {
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.INAPP)
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    itPurchaseList.forEach { itPurchase ->
                        if (productId == itPurchase.sku) {
                            val consumeParams =
                                    ConsumeParams.newBuilder()
                                            .setPurchaseToken(itPurchase.purchaseToken)
                                            .build()

                            billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    isInAppCached = false
                                    callback(null, outToken)
                                } else {
                                    callback(billingResult.responseCode, outToken)
                                    billingHandler.onBillingError(billingResult.responseCode)
                                }
                            }
                        }
                    }
                }
            } else {
                callback(purchaseResult.responseCode, null)
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            callback(110, null)
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }

    fun isSubscribedAnyRealtime(productId: ArrayList<String>): Boolean {
        var check = false
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.SUBS)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    cachedSubscriptionStatusList.clear()
                    isSubscriptionCached = true
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId.contains(itPurchase.sku) && itPurchase.purchaseState == PURCHASED
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedSubscriptionStatusList.add(itPurchase.sku)
                            if (!itPurchase.isAcknowledged) {
                                acknowledgePurchase(itPurchase, NO_CALL_BACK)
                            }
                        }
                        Log.e("SubscriptionTest", productId.contains(itPurchase.sku).toString() + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString() + " SkuSize: " + cachedSubscriptionStatusList.size)
                    }
                }
                Log.e("SubscriptionTest", "Subscription Cache List $cachedSubscriptionStatusList")
            } else {
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun isAnySubscribedRealtime")
            billingHandler.onBillingError(110)
        }
        Log.e("SubscriptionTest", "Check Returned")
        return check
    }

    fun isPurchasedAnyRealtime(productId: ArrayList<String>): Boolean {
        var check = false
        if (isConnected) {
            val purchaseResult = billingClient.queryPurchases(SkuType.INAPP)
            Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
            if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseResult.purchasesList?.let { itPurchaseList ->
                    cachedInAppStatusList.clear()
                    isInAppCached = true
                    Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                    itPurchaseList.forEach { itPurchase ->
                        if (!check) {
                            check = productId.contains(itPurchase.sku) && itPurchase.purchaseState == PURCHASED
                        }
                        if (itPurchase.purchaseState == PURCHASED) {
                            cachedInAppStatusList.add(itPurchase.sku)
                            if (!itPurchase.isAcknowledged) {
                                acknowledgePurchase(itPurchase, NO_CALL_BACK)
                            }
                        }
                        Log.e("SubscriptionTest", itPurchase.sku + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    }
                }
            } else {
                billingHandler.onBillingError(purchaseResult.responseCode)
            }
        } else {
            Log.e("SubscriptionTest", "fun isAnyPurchaseRealtime")
            billingHandler.onBillingError(110)
        }
        return check
    }

    fun subscribe(productId: String) {
        if (isConnected) {
            purchaseType = SkuType.SUBS
            savedProductId = productId
            var skuDetails: SkuDetails? = null
            getSubscriptionsSkuDetails(arrayListOf(productId)) { errorCode: Int?, skuList: List<SkuDetails>? ->
                if (errorCode == null) {
                    skuList?.forEach { itSkuDetail ->
                        if (itSkuDetail.sku == productId) {
                            skuDetails = itSkuDetail
                        }
                    }
                    skuDetails?.let {
                        val billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(it)
                                .build()
                        val response = billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
                        if (response != BillingClient.BillingResponseCode.OK) {
                            isSubscriptionCached = false
                            billingHandler.onBillingError(response)
                        }
                    }
                } else {
                    billingHandler.onBillingError(errorCode)
                }
            }
        } else {
            Log.e("SubscriptionTest", "fun subscribe")
            billingHandler.onBillingError(110)
        }
    }

    private fun reloadSubscriptionCache(): Boolean {
        val purchaseResult = billingClient.queryPurchases(SkuType.SUBS)
        Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
        if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchaseResult.purchasesList?.let { itPurchaseList ->
                Log.e("SubscriptionTest", "SubscribedList Size" + purchaseResult.purchasesList!!.size)
                cachedSubscriptionStatusList.clear()
                isSubscriptionCached = true
                itPurchaseList.forEach { itPurchase ->
                    Log.e("SubscriptionTest", itPurchase.sku + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    if (itPurchase.purchaseState == PURCHASED) {
                        cachedSubscriptionStatusList.add(itPurchase.sku)
                        if (!itPurchase.isAcknowledged) {
                            acknowledgePurchase(itPurchase, NO_CALL_BACK)
                        }
                    }
                    Log.e("SubscriptionTest", "SkuSize " + cachedSubscriptionStatusList.size.toString())
                }
            }
            Log.e("SubscriptionTest", "Subscription Cache List $cachedSubscriptionStatusList")
        } else {
            billingHandler.onBillingError(purchaseResult.responseCode)
        }
        return isSubscriptionCached
    }

    private fun reloadInAppCache(): Boolean {
        val purchaseResult = billingClient.queryPurchases(SkuType.INAPP)
        Log.e("SubscriptionTest", purchaseResult.responseCode.toString())
        if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchaseResult.purchasesList?.let { itPurchaseList ->
                Log.e("SubscriptionTest", "PurchasedList Size" + purchaseResult.purchasesList!!.size)
                cachedInAppStatusList.clear()
                isInAppCached = true
                itPurchaseList.forEach { itPurchase ->
                    Log.e("SubscriptionTest", itPurchase.sku + "==" + itPurchase.sku + " && " + itPurchase.purchaseState.toString() + "==" + PURCHASED.toString() + " && " + itPurchase.isAcknowledged.toString())
                    if (itPurchase.purchaseState == PURCHASED) {
                        cachedInAppStatusList.add(itPurchase.sku)
                        if (!itPurchase.isAcknowledged) {
                            acknowledgePurchase(itPurchase, NO_CALL_BACK)
                        }
                    }
                }
            }
        } else {
            billingHandler.onBillingError(purchaseResult.responseCode)
        }
        return isInAppCached
    }

    fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            ResponseCodes.SERVICE_TIMEOUT -> {
                "Service Timeout"
            }
            ResponseCodes.FEATURE_NOT_SUPPORTED -> {
                "Feature Not Supported"
            }
            ResponseCodes.SERVICE_DISCONNECTED -> {
                "Service Disconnected"
            }
            ResponseCodes.OK -> {
                "OK"
            }
            ResponseCodes.USER_CANCELED -> {
                "User Canceled"
            }
            ResponseCodes.SERVICE_UNAVAILABLE -> {
                "Service Unavailable"
            }
            ResponseCodes.BILLING_UNAVAILABLE -> {
                "Billing Unavailable"
            }
            ResponseCodes.ITEM_UNAVAILABLE -> {
                "Item Unavailable"
            }
            ResponseCodes.DEVELOPER_ERROR -> {
                "Developer Error"
            }
            ResponseCodes.ERROR -> {
                "Error"
            }
            ResponseCodes.ITEM_ALREADY_OWNED -> {
                "Item Already Owned"
            }
            ResponseCodes.ITEM_NOT_OWNED -> {
                "Item Not Owned"
            }
            ResponseCodes.NOT_PURCHASED_STATE -> {
                "purchase State is not Purchased"
            }
            ResponseCodes.NOT_CONNECTED -> {
                "Not Connected"
            }
            else -> {
                return ""
            }
        }
    }
}