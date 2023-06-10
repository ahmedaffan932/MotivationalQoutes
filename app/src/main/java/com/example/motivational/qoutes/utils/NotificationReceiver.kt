package com.example.motivational.qoutes.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.motivational.qoutes.R
import com.example.motivational.qoutes.activities.FullViewActivity
import com.example.motivational.qoutes.activities.MainActivity
import com.example.motivational.qoutes.activities.NewQuoteStudioActivity
import com.example.motivational.qoutes.database.QuotModel
import com.google.gson.Gson


class NotificationReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "my_channel_id"
    var NOTIFICATION_ID = 1

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d("logKey", "Broad Received")
        createNotificationChannel(context!!)

        val objQuote = Gson().fromJson(p1?.getStringExtra("quote"), QuotModel::class.java)
        val intent = Intent(context, FullViewActivity::class.java)
        intent.putExtra("quote", Gson().toJson(objQuote))

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_icon)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(objQuote.Quote)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(objQuote.Quote)
            )
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0,
                    intent, PendingIntent.FLAG_MUTABLE
                )
            )

        // Show the notification
        NOTIFICATION_ID = System.currentTimeMillis().toInt()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
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
}