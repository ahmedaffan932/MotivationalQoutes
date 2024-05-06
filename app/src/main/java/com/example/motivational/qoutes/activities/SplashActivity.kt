package com.example.motivational.qoutes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.ads.AdmobInterstitialAd
import com.example.motivational.qoutes.ads.AdmobNativeAds
import com.example.motivational.qoutes.ads.Ads
import com.example.motivational.qoutes.ads.InterstitialCallBack
import com.example.motivational.qoutes.ads.LoadAdCallBack
import com.example.motivational.qoutes.database.QuotModel
import com.example.motivational.qoutes.databinding.ActivitySplashBinding
import com.example.motivational.qoutes.utils.CustomDialog
import com.example.motivational.qoutes.utils.UtilLists
import com.example.motivational.qoutes.utils.Misc
import com.example.motivational.qoutes.utils.Misc.setupRoomDb
import com.example.motivational.qoutes.utils.Misc.unZipFolder
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var isIntAdLoaded = false
    private var isNativeAdLoaded = false
    private var isStartButtonVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (UtilSharedPerefs.getIsFirstTime(this)) {
            Firebase.analytics.logEvent("FirstTimeUser", null)
        } else {
            Firebase.analytics.logEvent("RetainedUser", null)
            UtilSharedPerefs.setIsFirstTime(this, false)
        }

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

        if (intent.getStringExtra("quote") != null) {
            val myLoader: CustomDialog = Misc.showProgressD(this)

            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // This method is called every second during the countdown
                    // You can update UI or perform any task here if needed
                }

                override fun onFinish() {
                    // This method is called after the countdown finishes
                    myLoader.dismiss()
                    val quoteExtra = intent.getStringExtra("quote")
                    val objQuote = Gson().fromJson(quoteExtra, QuotModel::class.java)
                    Log.d("logKeyQuoteSplash", objQuote.Quote)
                    Firebase.analytics.logEvent("UserFromNotification", null)

//                    val intent = Intent(this@SplashActivity, FullViewActivity::class.java)
//                    intent.putExtra("quote", quoteExtra)
//                    startActivity(intent)
//                    finish()
                }
            }.start()
        } else {
            object : CountDownTimer(6000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // This method is called every second during the countdown
                    // You can update UI or perform any task here if needed
                }

                override fun onFinish() {
                    // This method is called after the countdown finishes
//                    Firebase.analytics.logEvent("UserFromSplash", null)
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    finish()
                }
            }.start()
        }

        object : CountDownTimer(12000, 3000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                if (isIntAdLoaded && isNativeAdLoaded) {
                    showStartButton()
                }
            }

            override fun onFinish() {
                showStartButton()
            }
        }.start()

        AdmobNativeAds.loadAdmobNative(this, callBack = object : LoadAdCallBack {
            override fun onLoaded(interstitialAd: InterstitialAd?) {
                isNativeAdLoaded = true
                AdmobNativeAds.showNativeAd(
                    this@SplashActivity,
                    Ads.splashNative,
                    binding.nativeAdFrameLayout
                )
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            AdmobInterstitialAd.loadInterAdmob(this,
                callback = object : LoadAdCallBack {
                    override fun onLoaded(interstitialAd: InterstitialAd?) {
                        isIntAdLoaded = true
                    }
                })
        }, 1000)

        binding.btnStart.setOnClickListener {
            Ads.showInterstitial(this, Ads.dashboardIntAm, object : InterstitialCallBack{
                override fun onDismiss() {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            })
        }
    }


    private fun setupRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->

            if (task.isSuccessful && !BuildConfig.DEBUG) {
                remoteConfig.activate()
                FirebaseRemoteConfig.getInstance().activate()
                remoteConfig.activate()

                if (BuildConfig.VERSION_NAME == remoteConfig.getString("versionNameToBlockAds")) {
                    Ads.settingIntAm = ""
                    Ads.proMonthlyKey = ""
                    Ads.dashboardIntAm = ""
                    Ads.proLifeTimeKey = ""
                    Ads.proMonthlyPrice = ""
                    Ads.quoteStudioIntAm = ""
                    Ads.backSettingIntAm = ""
                    Ads.proLifeTimePrice = ""
                    Ads.dashboardNativeAm = ""
//                    Ads.quoteStudioNativeAm = ""
                    Ads.backQuoteStudioIntAm = ""
                    Ads.dashboardBannerAm = ""
                    Ads.inBetweenQuotesNativeAm = ""
                    Ads.quoteStudioCollapsingBannerAm = ""
                } else {
                    Ads.settingIntAm = remoteConfig.getString("settingIntAm").trim()
                    Ads.proMonthlyKey = remoteConfig.getString("proMonthlyKey").trim()
                    Ads.dashboardIntAm = remoteConfig.getString("dashboardIntAm").trim()
                    Ads.proLifeTimeKey = remoteConfig.getString("proLifeTimeKey").trim()
                    Ads.proMonthlyPrice = remoteConfig.getString("proMonthlyPrice").trim()
                    Ads.quoteStudioIntAm = remoteConfig.getString("quoteStudioIntAm").trim()
                    Ads.backSettingIntAm = remoteConfig.getString("backSettingIntAm").trim()
                    Ads.proLifeTimePrice = remoteConfig.getString("proLifeTimePrice").trim()
                    Ads.dashboardNativeAm = remoteConfig.getString("dashboardNativeAm").trim()
                    Ads.backQuoteStudioIntAm = remoteConfig.getString("backQuoteStudioIntAm").trim()
                    Ads.quoteStudioNativeAndBannerAm =
                        remoteConfig.getString("quoteStudioNativeAndBannerAm").trim()
                    Ads.dashboardBannerAm =
                        remoteConfig.getString("dashboardBannerAm").trim()
                    Ads.inBetweenQuotesNativeAm =
                        remoteConfig.getString("inBetweenQuotesNativeAm").trim()
                    Ads.quoteStudioCollapsingBannerAm =
                        remoteConfig.getString("quoteStudioCollapsingBannerAm").trim()
                }
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

    private fun showStartButton() {
        if (!isStartButtonVisible) {
            Handler(Looper.getMainLooper()).postDelayed({
                Misc.zoomInView(binding.btnStart, this, 250)
            }, 250)

            binding.tvLoading.visibility = View.INVISIBLE
            binding.animationView.visibility = View.INVISIBLE

            val a: Animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_logo)
            a.duration = 500

            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
        }
        isStartButtonVisible = true
    }

}