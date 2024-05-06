package com.example.motivational.qoutes.ads

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.motivational.qoutes.BuildConfig

object AdIds {

    var bannerAdIdAdOne = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/6300978111"
    } else {
        "ca-app-pub-6814505709397727/7172277692"
    }

    var nativeAdIdAdMobOne: String = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/2247696110"
    } else {
        "ca-app-pub-6814505709397727/8901605416"
    }

    var interstitialAdIdAdMobOne: String = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/1033173712"
    } else {
        "ca-app-pub-6814505709397727/4802847801"
    }
}