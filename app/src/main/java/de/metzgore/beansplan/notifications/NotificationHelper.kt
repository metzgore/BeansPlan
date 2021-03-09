package de.metzgore.beansplan.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import android.text.format.DateUtils
import de.metzgore.beansplan.MainActivity
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.settings.repository.AppSettings


object NotificationHelper {

    private const val REMINDER_CHANNEL_ID = "reminder_channel"

    private val twitchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/rocketbeanstv"))
    private val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/rocketbeanstv/live"))

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_reminder_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, name, importance).apply {
                description = context.getString(R.string.notification_channel_reminder_description)
                enableLights(true)
                enableVibration(true)
                setShowBadge(false)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(context: Context, appSettings: AppSettings, showWithReminder: ShowWithReminder): Notification {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val contentTitle = if (!showWithReminder.show.topic.isEmpty())
            "${showWithReminder.show.title} - ${showWithReminder.show.topic}"
        else
            showWithReminder.show.title

        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_notification)
            setContentTitle(contentTitle)
            setContentText(context.getString(R.string.notification_content_text,
                    DateUtils.formatDateTime(context, showWithReminder.show.timeStart.time, DateUtils.FORMAT_SHOW_TIME)))
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_MAX
            setSound(Uri.parse(appSettings.ringtonePreferenceValue))
            setContentIntent(pendingIntent)
            setWhen(showWithReminder.reminder!!.timestamp.time)
            setCategory(NotificationCompat.CATEGORY_REMINDER)
            addAction(R.drawable.ic_youtube, context.getString(R.string.notification_action_youtube),
                    PendingIntent.getActivity(context, 1, youtubeIntent, 0))
            addAction(R.drawable.ic_twitch_notification, context.getString(R.string.notification_action_twitch),
                    PendingIntent.getActivity(context, 2, twitchIntent, 0))
            if (appSettings.shouldVibrateOnNotification()) {
                setVibrate(longArrayOf(0, 250, 250, 250))
            } else {
                setVibrate(longArrayOf(0))
            }
        }

        return builder.build()
    }

    fun scheduleNotification(context: Context, appSettings: AppSettings, showWithReminder: ShowWithReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP,
                showWithReminder.reminder!!.timestamp.time, getPendingIntent(context, appSettings, showWithReminder))
    }

    private fun getPendingIntent(context: Context, appSettings: AppSettings, showWithReminder: ShowWithReminder): PendingIntent {
        val notificationIntent = Intent(context, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, showWithReminder.reminder!!.id)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, buildNotification(context, appSettings, showWithReminder))
        return PendingIntent.getBroadcast(context, showWithReminder.reminder.id.toInt(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun unscheduleNotification(context: Context, appSettings: AppSettings, showWithReminder: ShowWithReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context, appSettings, showWithReminder))
    }
}