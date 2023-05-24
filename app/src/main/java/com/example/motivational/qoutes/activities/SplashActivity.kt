package com.example.motivational.qoutes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.database.QuotViewModel
import com.example.motivational.qoutes.databinding.ActivitySplashBinding
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs
import com.example.motivational.qoutes.utils.UtilMiscs.readJsonFromFile
import com.example.motivational.qoutes.utils.UtilMiscs.setupRoomDb
import com.example.motivational.qoutes.utils.UtilMiscs.unZipFolder
import com.example.motivational.qoutes.utils.UtilMiscs.unzipFromAssets
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var quotOfSp=UtilSharedPerefs.getQuote(this)
        Log.d("logkey","quotOfSp: $quotOfSp, UtilLists.quoteSplash.size-1: ${UtilLists.quoteSplash.size-1}")
        binding.splashQuote.text=UtilLists.quoteSplash[quotOfSp]
        binding.imageView.setImageResource(UtilLists.wallSplash[quotOfSp])
        UtilSharedPerefs.setQuote(this, if (quotOfSp>=UtilLists.quoteSplash.size-1) 0 else quotOfSp+1)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setupRemoteConfig()
        unZipFolder()
        setupRoomDb(application)


        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // This method is called every second during the countdown
                // You can update UI or perform any task here if needed
            }

            override fun onFinish() {
                // This method is called after the countdown finishes
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }.start()
    }



    private fun setupRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful && !BuildConfig.DEBUG) {
                remoteConfig.activate()
                FirebaseRemoteConfig.getInstance().activate()
                remoteConfig.activate()


//                Ads.admob_interstitial_id =
//                    remoteConfig.getString(("admob_interstitial_id")).trim()
//                Ads.admob_native_id = remoteConfig.getString(("admob_native_id")).trim()

                Ads.dashboardIntAm=remoteConfig.getString("dashboardIntAm").trim()
                Ads.dashboardNativeAm=remoteConfig.getString("dashboardNativeAm").trim()

                loadAds()

            } else {
                loadAds()
            }


        }
    }

    private fun loadAds() {
        loadNativeAds()
        InterstitialAds.loadInterAdmob(this)

    }

    private fun loadNativeAds() {
        NativeAd.loadAdmobNativePreFetch(this@SplashActivity)
    }


}