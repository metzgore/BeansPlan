package de.metzgore.beansplan.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.util.archcomponents.Event
import java.util.*

class RemindersViewModel(repo: RemindersRepository) : ViewModel() {

    private val remindersRepo = repo
    private val _triggerDeletionDialog = MutableLiveData<Event<String>>()
    private val _triggerTimePickerDialog = MutableLiveData<Event<Date>>()

    val reminders: LiveData<List<ShowWithReminder>> = remindersRepo.loadReminders()
    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) { reminders ->
        reminders == null || reminders.isEmpty()
    }

    val triggerDeletionDialog: LiveData<Event<String>>
        get() = _triggerDeletionDialog

    val triggerTimePickerDialog: LiveData<Event<Date>>
        get() = _triggerTimePickerDialog

    fun triggerDeletionDialog(text: String) {
        _triggerDeletionDialog.value = Event(text)
    }

    fun updateReminder(show: Show, reminder: Reminder) {
        remindersRepo.upsertReminder(show, reminder)
    }

    fun deleteReminder(show: Show, reminder: Reminder) {
        remindersRepo.deleteReminder(show, reminder)
    }

    fun triggerTimePickerDialog(reminder: Reminder) {
        _triggerTimePickerDialog.value = Event(reminder.timestamp)
    }
}