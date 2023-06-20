package com.example.motivational.qoutes.utils

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.motivational.qoutes.BuildConfig
import com.example.motivational.qoutes.database.QuotViewModel
import com.google.gson.Gson
import java.util.*

class NotificationScheduler {
    private val NOTIFICATION_ID1 = 0

    fun scheduleNotification(application: Application) {
        Log.d("logKey", "Scheduling")
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val milliseconds: Long = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val min = calendar[Calendar.MINUTE]

        // Set the notification 1 hour after opening the app.
        val notificationTime1 = if (BuildConfig.DEBUG) {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
                set(Calendar.SECOND, 0)
            }
        } else {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour + 1)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

        val intent1 = Intent(application, NotificationReceiver::class.java)

        val pendingIntent1 = PendingIntent.getBroadcast(
            application,
            NOTIFICATION_ID1,
            intent1,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        alarmManager.cancel(pendingIntent1)

        // Schedule the notification
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            notificationTime1.timeInMillis,
            0,
            pendingIntent1
        )

    }
}