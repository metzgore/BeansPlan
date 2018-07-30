package de.metzgore.beansplan.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import dagger.android.DaggerBroadcastReceiver
import de.metzgore.beansplan.reminders.RemindersRepository
import javax.inject.Inject


class NotificationPublisher : DaggerBroadcastReceiver() {

    @Inject
    lateinit var reminderRepo: RemindersRepository

    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = intent.getParcelableExtra(NOTIFICATION) as Notification
        val id = intent.getLongExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id.toInt(), notification)

        reminderRepo.deleteReminder(id)
    }
}
