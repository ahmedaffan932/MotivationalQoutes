package com.example.motivational.qoutes.ads

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import com.example.motivational.qoutes.BuildConfig

object Ads {
    var dashboardBannerAm: String = "am"
    var splashNative: String ="am_small_hctr"
    var quoteStudioCollapsingBannerAm: String = ""

    var proMonthlyPrice = "21.99"
    var proLifeTimePrice = "0.99"
    var inBetweenQuotesNativeAm = "am_hctr"
    var inBetweenQuotesNativeAdPosition = 0
    var inBetweenQuotesNativeAdStartingIndex = 2

    var proMonthlyKey = "android.test.purchased"
    var proLifeTimeKey = "android.test.purchased"

    var settingIntAm = "am"
    var quoteStudioIntAm = "am"
    var backSettingIntAm = "am"
    var backQuoteStudioIntAm = "am"
    var dashboardIntAm = "am"
    var dashboardNativeAm = "am_small_lctr"
    var quoteStudioNativeAndBannerAm = "am"

    fun showInterstitial(
        activity: Activity,
        remoteKey: String,
        callback: InterstitialCallBack? = null
    ) {
        if (remoteKey.contains("am")) {
            AdmobInterstitialAd.showInterstitial(activity, callback)
        } else {
            callback?.onDismiss()
        }
    }
    
}