package com.sedsoftware.common.tools

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.sedsoftware.common.domain.entity.Shot
import com.sedsoftware.common.domain.entity.toScheduledTime
import com.sedsoftware.common.tools.notification.RemindieNotification

class AndroidRemindiesScheduler(
    private val context: Context
) : RemindiesScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    override fun schedule(shot: Shot) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            shot.toScheduledTime(),
            createPendingIntent(shot)
        )
    }

    override fun cancel(shot: Shot) {
        alarmManager.cancel(createPendingIntent(shot))
    }

    private fun createPendingIntent(shot: Shot): PendingIntent {
        val intent = Intent(context.applicationContext, RemindieNotification::class.java).apply {
            putExtra(RemindieNotification.titleExtra, shot.remindie.title)
            putExtra(RemindieNotification.messageExtra, shot.remindie.description)
        }

        return PendingIntent.getBroadcast(
            context.applicationContext,
            shot.remindie.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Remindies"
            val descriptionText = "Remindies notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(RemindieNotification.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
