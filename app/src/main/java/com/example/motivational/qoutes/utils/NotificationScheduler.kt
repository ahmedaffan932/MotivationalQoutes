package com.example.motivational.qoutes.utils

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.motivational.qoutes.database.QuotViewModel
import com.google.gson.Gson
import java.util.Calendar

class NotificationScheduler {
    companion object {
        private const val NOTIFICATION_ID1 = 0
        private const val NOTIFICATION_ID2 = 1
        private const val NOTIFICATION_ID3 = 2
        private const val NOTIFICATION_ID4 = 3

        fun scheduleNotification(application: Application) {
            Log.d("logKey", "Scheduling")
            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Set the notification time to 7 am
            val notificationTime1 = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            // Set the notification time to 1:30 pm
            val notificationTime2 = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 13)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
            }
            // Set the notification time to 6 pm
            val notificationTime3 = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            // Set the notification time to 9 pm
            val notificationTime4 = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 21)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val obj1 = Gson().toJson(QuotViewModel(application).getRandomObject())
            val obj2 = Gson().toJson(QuotViewModel(application).getRandomObject())
            val obj3 = Gson().toJson(QuotViewModel(application).getRandomObject())
            val obj4 = Gson().toJson(QuotViewModel(application).getRandomObject())

            Log.d("logKey", "$obj1 obj1")
            // Create an intent for the BroadcastReceiver
            val intent1 = Intent(application, NotificationReceiver::class.java)
            try {
                intent1.putExtra("quote", obj1)
            } catch (ec: Exception) {
                Log.e("logKey", "Crashed")
                ec.printStackTrace()
            }
            val intent2 = Intent(application, NotificationReceiver::class.java)
            try {
                intent2.putExtra("quote", obj2)
            } catch (ec: Exception) {
                ec.printStackTrace()
            }
            val intent3 = Intent(application, NotificationReceiver::class.java)
            try {
                intent3.putExtra("quote", obj3)
            } catch (ec: Exception) {
                ec.printStackTrace()
            }
            val intent4 = Intent(application, NotificationReceiver::class.java)
            try {
                intent4.putExtra("quote", obj4)
            } catch (ec: Exception) {
                ec.printStackTrace()
            }

            val pendingIntent1 = PendingIntent.getBroadcast(
                application,
                NOTIFICATION_ID1,
                intent1,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val pendingIntent2 = PendingIntent.getBroadcast(
                application,
                NOTIFICATION_ID2,
                intent2,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val pendingIntent3 = PendingIntent.getBroadcast(
                application,
                NOTIFICATION_ID3,
                intent3,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val pendingIntent4 = PendingIntent.getBroadcast(
                application,
                NOTIFICATION_ID4,
                intent4,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            alarmManager.cancel(pendingIntent1)
            alarmManager.cancel(pendingIntent2)
            alarmManager.cancel(pendingIntent3)
            alarmManager.cancel(pendingIntent4)

            // Schedule the notification
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notificationTime1.timeInMillis,
                0,
                pendingIntent1
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notificationTime2.timeInMillis,
                0,
                pendingIntent2
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notificationTime3.timeInMillis,
                0,
                pendingIntent3
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                notificationTime4.timeInMillis,
                0,
                pendingIntent4
            )
        }
    }
}