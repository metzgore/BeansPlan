package de.metzgore.beansplan.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class ReminderBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Boot Completed..!", Toast.LENGTH_LONG).show()
        Log.d("TEST", "boot completed")

        NotificationHelper.createNotificationChannel(context)
    }
}
