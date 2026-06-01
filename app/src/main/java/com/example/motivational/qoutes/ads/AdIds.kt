package com.example.motivational.qoutes.ads

import com.example.motivational.qoutes.BuildConfig

object AdIds {

    var nativeAdIdAdMobOne: String = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/2247696110"
    } else {
        "ca-app-pub-9651956713870958/8676925504"
    }

    var interstitialAdIdAdMobOne: String = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/1033173712"
    } else {
        "ca-app-pub-9651956713870958/1084342107"
    }
}