package com.example.motivational.qoutes.ads

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.utils.UtilSharedPerefs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeAd


@SuppressLint("StaticFieldLeak")
object NativeAd {

    interface NativeAdsCallBack {
        fun onNativeFailed()
        fun onNativeLoaded()
    }


    var amNative: NativeAd? = null
    var amInner: NativeAd? = null


    @SuppressLint("MissingPermission")
    fun loadAdmobNative(
        context: Context,
        remoteKey: String,
        view: FrameLayout,
        call: NativeAdsCallBack
    ) {

            Log.e("interstitial_ad_id: ", Ads.admob_native_id)
            val adLoader = AdLoader.Builder(
                context,
                Ads.admob_native_id
            ).forNativeAd { ad: NativeAd ->
                amNative = ad
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    amNative = null
                    call.onNativeFailed()
                    Log.e("AM_NATIVE", "Failed: " + adError.code + " | " + adError.message)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    call.onNativeLoaded()
                    Log.e("AM_NATIVE", "Loaded")
                    inflateAmNative(context, remoteKey, amNative!!, view)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    amNative = null
                }
            }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            adLoader.loadAd(AdRequest.Builder().build())

    }

    @SuppressLint("MissingPermission")
    fun loadAdmobNativePreFetch(
        context: Context,
        call: NativeAdsCallBack? = null
    ) {
        if (UtilSharedPerefs.getPurchasedStatus(context)){
            return
        }

            Log.e("interstitial_ad_id: ", Ads.admob_native_id)
            val adLoader = AdLoader.Builder(
                context,
                Ads.admob_native_id
            ).forNativeAd { ad: NativeAd ->
                amInner = ad
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    amInner = null
                    call?.onNativeFailed()
                    Log.e("AM_NATIVE", "Failed: " + adError.code + " | " + adError.message)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    call?.onNativeLoaded()
                    Log.e("AM_NATIVE", "Loaded")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    amInner = null
                }
            }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            adLoader.loadAd(AdRequest.Builder().build())

    }

    fun inflateAmNative(
        context: Context,
        remoteKey: String,
        nativeAd: NativeAd,
        amLayout: FrameLayout
    ) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater


        val adView = when (remoteKey) {
            Ads.admobHctr -> {
                inflater.inflate(R.layout.admob_native_hctr, null) as NativeAdView
            }
            Ads.admobLctr -> {
                inflater.inflate(R.layout.admob_native_lctr, null) as NativeAdView
            }
            Ads.admob -> {
                inflater.inflate(R.layout.admob_small_native, null) as NativeAdView
            }
            else -> {
                inflater.inflate(R.layout.admob_native_hctr, null) as NativeAdView
            }
        }

        amLayout.removeAllViews()
        amLayout.addView(adView)

        adView.mediaView = adView.findViewById(R.id.ad_media)

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent)

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    fun showPreFetch(
        context: Context, remoteKey: String, amLayout: FrameLayout, call: NativeAdsCallBack?
    ) {
        if (amInner != null &&
            remoteKey.contains("am")
        ) {

            call?.onNativeLoaded()
            amLayout.visibility = View.VISIBLE

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                    as LayoutInflater

            val adView = when (remoteKey) {
                Ads.admobHctr -> {
                    inflater.inflate(R.layout.admob_native_hctr, null) as NativeAdView
                }
                Ads.admobLctr -> {
                    inflater.inflate(R.layout.admob_native_lctr, null) as NativeAdView
                }
                Ads.admob -> {
                    inflater.inflate(R.layout.admob_small_native, null) as NativeAdView
                }
                else -> {
                    inflater.inflate(R.layout.admob_native_hctr, null) as NativeAdView
                }
            }


            amLayout.removeAllViews()
            amLayout.addView(adView)

            adView.mediaView = adView.findViewById(R.id.ad_media)

            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)

            (adView.headlineView as TextView).text = amInner?.headline
            adView.mediaView?.setMediaContent(amInner?.mediaContent)

            if (amInner?.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = amInner?.body
            }

            if (amInner?.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = amInner?.callToAction
            }

            if (amInner?.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    amInner?.icon!!.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            adView.setNativeAd(amInner!!)
            loadAdmobNativePreFetch(context, call)
        } else {
            call?.onNativeFailed()
            amLayout.visibility = View.GONE
            loadAdmobNativePreFetch(context, call)
        }
    }


}