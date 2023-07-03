package com.example.motivational.qoutes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.databinding.ActivityInAppBinding
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInAppBinding
    private var activeSubScription = 1
    private lateinit var billingClient : BillingClient
    private var isBillingClientConnected = false
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                UtilSharedPerefs.setPurchasedStatus(this, true)
                GlobalScope.launch {
                    handlePurchase(purchases[0])
                }
//                Log.d(UtilMiscs.logKey, "Ya hooo.....")
                Toast.makeText(this, "Restarting Application.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Ads.proLifeTimePrice == ""){
            Ads.proMonthlyPrice = "21.99"
            Ads.proLifeTimePrice = "0.99"
        }

        binding.txtLifeTime.text="Life Time: $${Ads.proLifeTimePrice}"
        binding.txtMonthly.text="Monthly: $${Ads.proMonthlyPrice}"
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingClientConnected = true
//                    Log.d(UtilMiscs.logKey, "Billing Result Ok")
                }
            }

            override fun onBillingServiceDisconnected() {
//                Log.d(UtilMiscs.logKey, "Service disconnected")
            }
        })

        btnClicks()
    }

    private fun btnClicks() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }
        binding.btnLifeTime.setOnClickListener {
            activeSubScription=1
            binding.btnLifeTime.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_round_btn_ripple,theme)
            binding.btnMonthly.background=null
            binding.radioButtonLifeTime.isChecked=true
            binding.radioButtonMonthly.isChecked=false

            if (isBillingClientConnected) {
                GlobalScope.launch {
                    querySkuDetails()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        binding.btnMonthly.setOnClickListener {
            activeSubScription=2
            binding.btnMonthly.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_round_btn_ripple,theme)
            binding.btnLifeTime.background=null
            binding.radioButtonMonthly.isChecked=true
            binding.radioButtonLifeTime.isChecked=false

            if (isBillingClientConnected) {
                GlobalScope.launch {
                    querySkuDetails()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnContinue.setOnClickListener {
            if (isBillingClientConnected) {
                GlobalScope.launch {
                    querySkuDetails()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun querySkuDetails() {
        try {
            //
            val inAppKey =
                when (activeSubScription) {
                    1 -> {
                        Ads.proLifeTimeKey
                    }
                    else -> {
                        Ads.proMonthlyKey
                    }
                }

            val skuList = ArrayList<String>()

            skuList.add(inAppKey)

            val params = SkuDetailsParams.newBuilder()
            if (activeSubScription == 1) {
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            } else {
                params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
            }

            val skuDetailsResult = withContext(Dispatchers.IO) {
                billingClient.querySkuDetails(params.build())
            }

            val flowParams = skuDetailsResult.skuDetailsList?.get(0)?.let {
                BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
            }
            flowParams?.let {
                billingClient.launchBillingFlow(
                    this,
                    it
                ).responseCode

            }


        } catch (e: Exception) {
            e.printStackTrace()
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, "Not available yet.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
    }

}