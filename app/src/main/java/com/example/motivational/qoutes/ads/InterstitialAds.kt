package com.example.motivational.qoutes.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAds {

    var interAdmob: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    //load Admob Interstitial
    fun loadInterAdmob(context: Context, adId: String = Ads.admob_interstitial_id_one) {
        Log.d("loadAdmob?", "Already loaded")

        if (UtilSharedPerefs.getPurchasedStatus(context)) {
            return
        }
        val admobRequest = AdRequest.Builder().build()

        com.google.android.gms.ads.interstitial.InterstitialAd.load(
            context,
            adId,
            admobRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("loadAdmob?", adError.message + adError.code)
                    interAdmob = null
                    if (adId == Ads.admob_interstitial_id_one) {
                        loadInterAdmob(context, Ads.admob_interstitial_id_two)
                    } else if (adId == Ads.admob_interstitial_id_three) {
                        loadInterAdmob(context, Ads.admob_interstitial_id_three)
                    }
                }

                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    Log.d("loadAdmob?", "Ad was loaded.")
                    interAdmob = interstitialAd
                }
            })

    }


    fun showInterstitialAdmob(
        activity: Activity,
        context: Context,
        remote: String,
        callback: InterstitialCallback?
    ) {
        if (interAdmob != null && remote == "am") {
            interAdmob?.show(activity)
        } else {
            Log.d("interAdmobShow", "The interstitial ad wasn't ready yet.")
            callback?.onResult()
            loadInterAdmob(context)
        }

        interAdmob?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("interAdmobShow", "Ad was dismissed.")
                callback?.onResult()
                interAdmob = null
                loadInterAdmob(context)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("interAdmobShow", "Ad failed to show." + adError.message + adError.code)
                callback?.onResult()
                interAdmob = null
                loadInterAdmob(context)
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("interAdmobShow", "Ad showed fullscreen content.")
            }
        }

    }


}