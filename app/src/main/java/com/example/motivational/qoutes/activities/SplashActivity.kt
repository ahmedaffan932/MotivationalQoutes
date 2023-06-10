package com.example.motivational.qoutes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.BannerAd
import com.example.motivational.qoutes.ads.InterstitialAds
import com.example.motivational.qoutes.ads.NativeAd
import com.example.motivational.qoutes.databinding.ActivitySplashBinding
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.UtilMiscs.setupRoomDb
import com.example.motivational.qoutes.utils.UtilMiscs.unZipFolder
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAds()

        val quotOfSp = UtilSharedPerefs.getQuote(this)
        Log.d(
            "logkey",
            "quotOfSp: $quotOfSp, UtilLists.quoteSplash.size-1: ${UtilLists.quoteSplash.size - 1}"
        )
        binding.splashQuote.text = UtilLists.quoteSplash[quotOfSp]
        binding.imageView.setImageResource(UtilLists.wallSplash[quotOfSp])
        UtilSharedPerefs.setQuote(
            this,
            if (quotOfSp >= UtilLists.quoteSplash.size - 1) 0 else quotOfSp + 1
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()
        setupRemoteConfig()
        unZipFolder()
        setupRoomDb(application)
//
//
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
        remoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build())
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->

            if (task.isSuccessful && !BuildConfig.DEBUG) {
                remoteConfig.activate()
                FirebaseRemoteConfig.getInstance().activate()
                remoteConfig.activate()

                Ads.settingIntAm = remoteConfig.getString("settingIntAm").trim()
                Ads.proMonthlyKey = remoteConfig.getString("proMonthlyKey").trim()
                Ads.dashboardIntAm = remoteConfig.getString("dashboardIntAm").trim()
                Ads.proLifeTimeKey = remoteConfig.getString("proLifeTimeKey").trim()
                Ads.proMonthlyPrice = remoteConfig.getString("proMonthlyPrice").trim()
                Ads.quoteStudioIntAm = remoteConfig.getString("quoteStudioIntAm").trim()
                Ads.backSettingIntAm = remoteConfig.getString("backSettingIntAm").trim()
                Ads.proLifeTimePrice = remoteConfig.getString("proLifeTimePrice").trim()
                Ads.dashboardNativeAm = remoteConfig.getString("dashboardNativeAm").trim()
                Ads.quoteStudioBannerAm = remoteConfig.getString("quoteStudioBannerAm").trim()
                Ads.quoteStudioNativeAm = remoteConfig.getString("quoteStudioNativeAm").trim()
                Ads.backQuoteStudioIntAm = remoteConfig.getString("backQuoteStudioIntAm").trim()
                Ads.dashboardCollapsibleAm = remoteConfig.getString("dashboardCollapsibleAm").trim()
                Ads.inBetweenQuotesNativeAm = remoteConfig.getString("inBetweenQuotesNativeAm").trim()
                Ads.quoteStudioCollapsingBannerAm = remoteConfig.getString("quoteStudioCollapsingBannerAm").trim()

                try {
                    Ads.inBetweenQuotesNativeAdPosition =
                        remoteConfig.getString("inBetweenQuotesNativeAdPosition").toInt()
                } catch (exc: Exception) {
                    Ads.inBetweenQuotesNativeAdPosition = 0
                    exc.printStackTrace()
                }
                try {
                    Ads.inBetweenQuotesNativeAdStartingIndex =
                        remoteConfig.getString("inBetweenQuotesNativeAdStartingIndex").toInt()
                } catch (exc: Exception) {
                    Ads.inBetweenQuotesNativeAdStartingIndex = 0
                    exc.printStackTrace()
                }
            }
        }
    }

    private fun loadAds() {
        loadNativeAds()
        InterstitialAds.loadInterAdmob(this)
        BannerAd.load(this)

    }

    private fun loadNativeAds() {
        NativeAd.loadNativeAd(this@SplashActivity)
    }


}