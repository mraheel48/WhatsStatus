package com.risetech.statussaver.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.risetech.statussaver.R
import com.risetech.statussaver.billing.GoogleBilling
import com.risetech.statussaver.utils.Constants
import com.risetech.statussaver.utils.Utils
import java.lang.Exception
import java.util.ArrayList

class ProScreen : AppCompatActivity(), GoogleBilling.GoogleBillingHandler {

    lateinit var btnBack: ImageView
    lateinit var btnBuy: TextView

    lateinit var bp: GoogleBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_screen)

        //Billing init
        bp = GoogleBilling(this@ProScreen, this@ProScreen, this)
        bpInit()

        btnBack = findViewById(R.id.imageView26)
        btnBuy = findViewById(R.id.buy_btn)

        btnBack.setOnClickListener {
            finish()
        }

        btnBuy.setOnClickListener {

            if (bp.isConnected) {
                if (!bp.isPurchased(Constants.inAppKey)) {
                    bp.purchase(Constants.inAppKey)
                } else {
                    Utils.showToast(this, "Already Purchased")
                }
            } else {
                Utils.showToast(this, "BILLING UNAVAILABLE")
            }

        }

    }

    private fun bpInit() {
        if (!bp.isConnected) {
            bp.startConnection()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBillingInitialized() {

        if (bp.isPurchased(Constants.inAppKey)) {
            btnBuy.text = "Already Purchased"
            btnBuy.isClickable = false
        } else {

            try {

                if (bp.isConnected && !bp.isPurchased(Constants.inAppKey)) {

                    try {

                        val abc = ArrayList<String>()
                        abc.add(Constants.inAppKey)

                        bp.getInAppSkuDetails(abc) { error: Int?, skuList: List<SkuDetails>? ->

                            if (skuList != null && skuList.isNotEmpty()) {
                                Constants.inAppPrices = skuList[0].price
                                btnBuy.text = skuList[0].price

                            } else {
                                Log.e("error", error.toString())
                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    override fun onPurchased(purchase: Purchase) {
        if (bp.isConnected && bp.isPurchased(Constants.inAppKey)) {
            finish()
        }
    }

    override fun onBillingServiceDisconnected() {

    }

    override fun onBillingError(errorCode: Int) {

    }


}