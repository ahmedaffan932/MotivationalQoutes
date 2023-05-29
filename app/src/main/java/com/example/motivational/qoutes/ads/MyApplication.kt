package com.example.motivational.qoutes.ads

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            Log.d("logkey","Admob Initialized status: $it")
        }
    }
}