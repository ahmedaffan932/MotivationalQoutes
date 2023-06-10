package com.example.motivational.qoutes.ads

import com.example.motivational.qoutes.BuildConfig

object Ads {
    var quoteStudioBannerAm: String = ""
    var dashboardCollapsibleAm: String = ""
    var quoteStudioCollapsingBannerAm: String = ""

    var bannerAdIdOne = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111" else "ca-app-pub-6814505709397727/7172277692"
    var bannerAdIdTwo = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111" else "ca-app-pub-6814505709397727/6295090542"
    var bannerAdIdThree = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111" else "ca-app-pub-6814505709397727/3312907822"

    var inBetweenQuotesNativeAdPosition = 0
    var inBetweenQuotesNativeAdStartingIndex = 2
    var inBetweenQuotesNativeAm = "am_hctr"
    var proLifeTimeKey = "android.test.purchased"
    var proMonthlyKey = "android.test.purchased"
    var proLifeTimePrice = ""
    var proMonthlyPrice = ""

    const val admob = "am"
    const val admobSmallHctr = "am_small_hctr"
    const val admobHctr = "am_hctr"
    const val admobLctr = "am_lctr"

    var admob_interstitial_id_one =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else "ca-app-pub-6814505709397727/4802847801"

    var admob_interstitial_id_two =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else "ca-app-pub-6814505709397727/7867994849"

    var admob_interstitial_id_three =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712" else "ca-app-pub-6814505709397727/6885726872"

    var admob_native_id_one =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else "ca-app-pub-6814505709397727/8901605416"

    var admob_native_id_two =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else "ca-app-pub-6814505709397727/1510200552"

    var admob_native_id_three =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else "ca-app-pub-6814505709397727/3519512728"

    var appOpenAdIdOne = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/3419835294"
    } else {
        "ca-app-pub-6814505709397727/9112562244"
    }

    var appOpenAdIdTwo = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/3419835294"
    } else {
        "ca-app-pub-6814505709397727/3474018916"
    }

    var appOpenAdIdThree  = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/3419835294"
    } else {
        "ca-app-pub-6814505709397727/7110011599"
    }

    var settingIntAm = "am"
    var quoteStudioIntAm = "am"
    var backSettingIntAm = "am"
    var backQuoteStudioIntAm = "am"
    var dashboardIntAm = "am"
    var dashboardNativeAm = "am_small_hctr"
    var quoteStudioNativeAm = "am"

}