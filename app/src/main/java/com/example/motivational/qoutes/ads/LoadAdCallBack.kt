package com.example.motivational.qoutes.ads

import com.google.android.gms.ads.interstitial.InterstitialAd

interface LoadAdCallBack {
    fun onLoaded(interstitialAd: InterstitialAd? = null){}
    fun onFailed(){}
}