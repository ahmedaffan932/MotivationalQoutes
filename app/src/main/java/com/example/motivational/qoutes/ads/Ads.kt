package com.example.motivational.qoutes.ads

import com.example.motivational.qoutes.BuildConfig

object Ads {
    var inBetweenQuotesNativeAdPosition = 3
    var inBetweenQuotesNativeAdStartingIndex = 2
    var inBetweenQuotesNativeAm = "am_hctr"
    var proLifeTimeKey = "android.test.purchased"
    var proMonthlyKey = "android.test.purchased"
    var proLifeTimePrice = ""
    var proMonthlyPrice = ""

    const val PURCHASE = "Purchase"

    const val admob = "am"
    const val admobHctr = "am_hctr"
    const val admobLctr = "am_lctr"

    var admob_interstitial_id =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else "ca-app-pub-6814505709397727/6885726872"
    var admob_native_id =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else "ca-app-pub-6814505709397727/3519512728"

    var settingIntAm = "am"
    var quoteStudioIntAm = "am"
    var backSettingIntAm = "am"
    var backQuoteStudioIntAm = "am"
    var dashboardIntAm = "am"
    var dashboardNativeAm = "am"

}