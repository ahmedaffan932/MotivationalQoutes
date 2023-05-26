package com.example.motivational.qoutes.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class NotificationScheduler {
    companion object {
        private const val NOTIFICATION_ID = 0

        fun scheduleNotification(context: Context) {
            Log.d("logkey", "Sheduling")
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Set the notification time to 9 am
            val notificationTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            // Create an intent for the BroadcastReceiver
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            // Schedule the notification
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent
            )
        }
    }
}