package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show

interface OnReminderButtonClickListener {
    fun onUpsertReminder(show: Show, reminder: Reminder)

    fun deleteReminder(show: Show, reminder: Reminder)
}
