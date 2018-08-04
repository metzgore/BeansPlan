package de.metzgore.beansplan.reminders

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.util.archcomponents.Event

class RemindersViewModel(repo: RemindersRepository) : ViewModel() {

    private val remindersRepo = repo
    private val _triggerDeletionDialog = MutableLiveData<Event<ShowWithReminder>>()
    private val _triggerTimePickerDialog = MutableLiveData<Event<ShowWithReminder>>()
    private val _triggerDeletionOrUpdateDialog = MutableLiveData<Event<ShowWithReminder>>()
    val reminders: LiveData<List<ShowWithReminder>> = remindersRepo.loadRemindersAsLiveData()
    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) { reminders ->
        reminders == null || reminders.isEmpty()
    }

    val triggerDeletionDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerDeletionDialog

    val triggerTimePickerDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerTimePickerDialog

    val triggerDeletionOrUpdateDialog: LiveData<Event<ShowWithReminder>>
        get() = _triggerDeletionOrUpdateDialog

    fun triggerDeletionDialog(showWithReminder: ShowWithReminder) {
        _triggerDeletionDialog.value = Event(showWithReminder)
    }

    fun upsertReminder(showWithReminder: ShowWithReminder) {
        remindersRepo.upsertReminder(showWithReminder)
    }

    fun deleteReminder(showWithReminder: ShowWithReminder) {
        remindersRepo.deleteReminder(showWithReminder)
    }

    fun triggerTimePickerDialog(showWithReminder: ShowWithReminder) {
        _triggerTimePickerDialog.value = Event(showWithReminder)
    }

    fun triggerDeletionOrUpdateDialog(showWithReminder: ShowWithReminder) {
        _triggerDeletionOrUpdateDialog.value = Event(showWithReminder)
    }
}