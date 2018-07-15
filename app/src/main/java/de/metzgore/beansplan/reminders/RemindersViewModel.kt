package de.metzgore.beansplan.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.relations.ShowWithReminder

class RemindersViewModel(repo: RemindersRepository) : ViewModel() {

    private val remindersRepo = repo

    val reminders: LiveData<List<ShowWithReminder>> = remindersRepo.loadReminders()
    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) { reminders ->
        reminders == null || reminders.isEmpty()
    }

    fun updateReminder(show: Show, reminder: Reminder) {
        remindersRepo.upsertReminder(show, reminder)
    }

    fun deleteReminder(show: Show, reminder: Reminder) {
        remindersRepo.deleteReminder(show, reminder)
    }
}