package com.example.motivational.qoutes.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.activities.SplashActivity
import com.example.motivational.qoutes.database.QuotViewModel
import com.google.gson.Gson


class NotificationReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "my_channel_id"
    var NOTIFICATION_ID = 1
    lateinit var mContext: Context

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d("logKey", "Broad Received")
        createNotificationChannel(context!!)

        mContext = context

//        val objQuote = Gson().fromJson(p1?.getStringExtra("quote"), QuotModel::class.java)
        val objQuote = QuotViewModel(mContext.applicationContext as Application).getRandomObject()

        Log.d("logKeyQuoteReceiver", objQuote.Quote)
        val intent = Intent(context, SplashActivity::class.java)
        intent.putExtra("quote", Gson().toJson(objQuote))

        customNotification(objQuote.Category, objQuote.Quote, intent, 123456)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "My Channel"
        val descriptionText = "Sample Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    //custom view
    @SuppressLint("RemoteViewLayout")
    private fun customNotification(
        category: String, quote: String, intent: Intent, notificationID: Int
    ) {

        Log.e("CUSTOM", "NOTIFICATION CALLED")
        val pendingIntent =
            PendingIntent.getActivity(
                mContext,
                0 /* request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val uiModeManager = mContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val uiModeNight = uiModeManager.nightMode

        // Check if system UI is in night mode
        val remoteViews =
        if (uiModeNight == UiModeManager.MODE_NIGHT_YES) {
            RemoteViews(BuildConfig.APPLICATION_ID, R.layout.custom_notification_layout_dark)
        } else {
            RemoteViews(BuildConfig.APPLICATION_ID, R.layout.custom_notification_layout_light)
        }

        val remoteViewSmall =
            if (uiModeNight == UiModeManager.MODE_NIGHT_YES) {
                RemoteViews(BuildConfig.APPLICATION_ID, R.layout.small_custom_notification_layout_dark)
            } else {
                RemoteViews(BuildConfig.APPLICATION_ID, R.layout.small_custom_notification_layout_light)
            }




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

        val builder = NotificationCompat.Builder(mContext, category)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setCustomContentView(remoteViewSmall)
            .setCustomBigContentView(remoteViews)

        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

}