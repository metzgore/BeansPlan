package de.metzgore.beansplan.notifications

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import dagger.android.DaggerBroadcastReceiver
import de.metzgore.beansplan.reminders.RemindersRepository
import de.metzgore.beansplan.settings.repository.AppSettings
import javax.inject.Inject


class ReminderBootReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var reminderRepo: RemindersRepository

    @Inject
    lateinit var appSettings: AppSettings

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        AsyncTask.execute {
            val showWithReminder = reminderRepo.loadRemindersSync()
            showWithReminder.forEach {
                NotificationHelper.scheduleNotification(context, appSettings, it)
            }
        }
    }
}
