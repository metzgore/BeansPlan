package de.metzgore.beansplan.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.util.archcomponents.Event

class RemindersViewModel(private val repo: RemindersRepository) : ViewModel() {

    private val _triggerDeletionDialog = MutableLiveData<Event<ShowWithReminder>>()
    private val _triggerTimePickerDialog = MutableLiveData<Event<ShowWithReminder>>()
    private val _triggerDeletionOrUpdateDialog = MutableLiveData<Event<ShowWithReminder>>()
    private val _triggerReminderInserted = MutableLiveData<Event<Boolean>>()
    private val _triggerReminderDeleted = MutableLiveData<Event<Boolean>>()
    private val _triggerReminderUpdated = MutableLiveData<Event<Boolean>>()
    val reminders: LiveData<List<ShowWithReminder>> = repo.loadReminders()
    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) { reminders ->
        reminders == null || reminders.isEmpty()
    }

    val triggerDeletionDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerDeletionDialog

    val triggerTimePickerDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerTimePickerDialog

    val triggerDeletionOrUpdateDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerDeletionOrUpdateDialog

    val triggerReminderInserted: LiveData<Event<Boolean>>
        get() = _triggerReminderInserted

    val triggerReminderDeleted: LiveData<Event<Boolean>>
        get() = _triggerReminderDeleted

    val triggerReminderUpdated: LiveData<Event<Boolean>>
        get() = _triggerReminderUpdated

    fun triggerDeletionDialog(showWithReminder: ShowWithReminder) {
        _triggerDeletionDialog.value = Event(showWithReminder)
    }

    fun triggerTimePickerDialog(showWithReminder: ShowWithReminder) {
        _triggerTimePickerDialog.value = Event(showWithReminder)
    }

    fun triggerDeletionOrUpdateDialog(showWithReminder: ShowWithReminder) {
        _triggerDeletionOrUpdateDialog.value = Event(showWithReminder)
    }

    fun upsertReminder(showWithReminder: ShowWithReminder) {
        repo.upsertReminder(showWithReminder)
    }

    fun deleteReminder(showWithReminder: ShowWithReminder) {
        repo.deleteReminder(showWithReminder)
    }

    fun triggerReminderInserted() {
        _triggerReminderInserted.value = Event(true)
    }

    fun triggerReminderDeleted() {
        _triggerReminderDeleted.value = Event(true)
    }

    fun triggerReminderUpdated() {
        _triggerReminderUpdated.value = Event(true)
    }
}