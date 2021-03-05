package de.metzgore.beansplan.reminders

import androidx.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.testing.OpenForTesting
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class RemindersRepository @Inject constructor(private val dao: ScheduleRoomDao, private val
appExecutors: AppExecutors) {

    fun loadReminders(): LiveData<List<ShowWithReminder>> {
        return dao.getShowsWithRemindersDistict()
    }

    fun loadRemindersSync(): List<ShowWithReminder> {
        return dao.getShowsWithRemindersSync()
    }

    fun upsertReminder(showWithReminder: ShowWithReminder) {
        appExecutors.diskIO().execute { dao.upsertReminder(showWithReminder) }
    }

    fun deleteReminder(showWithReminder: ShowWithReminder) {
        appExecutors.diskIO().execute { dao.deleteReminder(showWithReminder) }
    }

    fun deleteReminder(reminderId: Long) {
        appExecutors.diskIO().execute { dao.deleteReminder(reminderId) }
    }
}