package com.example.motivational.qoutes.fcm.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.activities.SplashActivity
import com.example.motivational.qoutes.database.QuotViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.concurrent.atomic.AtomicInteger


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
open class CustomMessagingService : FirebaseMessagingService() {

    companion object {
        const val ICON_KEY = "icon"
        const val APP_TITLE_KEY = "title"
        const val SHORT_DESC_KEY = "short_desc"
        const val LONG_DESC_KEY = "long_desc"
        const val APP_FEATURE_KEY = "feature"
        const val APP_URL_KEY = "app_url"
        const val IS_PREMIUM = "is_premium"
        private val seed = AtomicInteger()

        fun getNextInt(): Int {
            return seed.incrementAndGet()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("CUSTOM", "NOTIFICATION")
//        val data = remoteMessage.data
//        if (data.isNotEmpty()) {
//            val iconURL = data[ICON_KEY]
//            val title = data[APP_TITLE_KEY]
//            val shortDesc = data[SHORT_DESC_KEY]
//            val longDesc = data[LONG_DESC_KEY]
//            val feature = data[APP_FEATURE_KEY]
//            val appURL = data[APP_URL_KEY]
//            val notificationID = getNextInt()
//
//            if (iconURL != null && title != null && shortDesc != null && feature != null && appURL != null) {
//                val standard = "https://play.google.com/store/apps/details?id="
//
//                try {
//                    val id = appURL.substring(standard.length)
//                    if (BuildConfig.DEBUG) Log.e("package sent ", id)
//
//                    if (!isAppInstalled(id, this) && !DB.getInstance(this).getBoolean(IS_PREMIUM))
//                        Handler(this.mainLooper).post {
//                            customNotification(
//                                iconURL,
//                                title, shortDesc, longDesc,
//                                feature, appURL, notificationID
//                            )
//                        }
//
//                } catch (e: Exception) {
//                    if (BuildConfig.DEBUG) Log.e("FcmFireBase", "package not valid")
//                }
//            }
//        }

//        if (BuildConfig.DEBUG) Log.e("From: ", remoteMessage.from.toString())
//        if (remoteMessage.notification != null) {
//            if (BuildConfig.DEBUG) Log.e("Message  Body:", remoteMessage.notification!!.body.toString())
//        }

        val objQuote = QuotViewModel(application).getRandomObject()

        Log.d("logKeyQuoteReceiver", objQuote.Quote)
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("quote", Gson().toJson(objQuote))
        customNotification(objQuote.Category, objQuote.Quote, intent, 123456)
    }


    //custom view
    private fun customNotification(
        category: String, quote: String, intent: Intent, notificationID: Int
    ) {

        Log.e("CUSTOM", "NOTIFICATION CALLED")
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0 /* request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val remoteViews =
            RemoteViews(BuildConfig.APPLICATION_ID, R.layout.custom_notification_layout_light)

        val remoteViewSmall =
            RemoteViews(BuildConfig.APPLICATION_ID, R.layout.small_custom_notification_layout_light)

        remoteViews.setTextViewText(
            R.id.tv_title,
            category.substring(0, 1).uppercase() + category.substring(1)
        )
        remoteViews.setTextViewText(R.id.tv_short_desc, quote)

        remoteViewSmall.setTextViewText(
            R.id.tv_title,
            category.substring(0, 1).uppercase() + category.substring(1)
        )
        remoteViewSmall.setTextViewText(R.id.tv_short_desc, quote)

        val builder = NotificationCompat.Builder(this, category)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setCustomContentView(remoteViewSmall)
            .setCustomBigContentView(remoteViews)

        val mNotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            category,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mNotificationManager.createNotificationChannel(channel)

        mNotificationManager.notify(notificationID, builder.build())

//        Picasso.get()
//            .load(iconURL)
//            .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
//        Picasso.get()
//            .load(feature)
//            .into(remoteViews, R.id.iv_feature, notificationID, builder.build())


    }


    private fun isAppInstalled(uri: String, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val applicationInfo = pm.getApplicationInfo(uri, 0)
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }
}