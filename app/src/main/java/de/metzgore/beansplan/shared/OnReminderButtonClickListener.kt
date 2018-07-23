package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.room.relations.ShowWithReminder

interface OnReminderButtonClickListener {
    fun deleteOrUpdateReminder(showWithReminder: ShowWithReminder)

    fun onUpsertReminder(showWithReminder: ShowWithReminder)

    fun deleteReminder(showWithReminder: ShowWithReminder)
}
