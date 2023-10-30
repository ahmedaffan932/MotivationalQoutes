package com.example.motivational.qoutes.ads

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.example.motivational.qoutes.fcm.services.FcmFireBaseID

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FcmFireBaseID.subscribeToTopic()
        FirebaseApp.initializeApp(this)
    }

}