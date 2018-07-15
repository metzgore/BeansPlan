package de.metzgore.beansplan.reminders

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.Show
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

    fun upsertReminder(show: Show, reminder: Reminder) {
        appExecutors.diskIO().execute { dao.upsertReminder(show, reminder) }
    }

    fun deleteReminder(show: Show, reminder: Reminder) {
        appExecutors.diskIO().execute { dao.deleteReminder(show, reminder) }
    }
}