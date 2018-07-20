package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.relations.ShowWithReminder

interface OnReminderButtonClickListener {
    fun onUpsertReminder(show: Show, reminder: Reminder)

    fun onUpsertReminder(showWithReminder: ShowWithReminder)

    fun deleteReminder(showWithReminder: ShowWithReminder)
}
