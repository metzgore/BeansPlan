package de.metzgore.beansplan.dailyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.testing.OpenForTesting
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DailyScheduleRepository @Inject constructor(private val dao: ScheduleRoomDao, private val
appExecutors: AppExecutors) {

    fun loadScheduleFromCache(date: Date): LiveData<DailyScheduleWithShows> {
        return dao.getDailyScheduleWithShowsDistinct(date)
    }

    fun upsertReminder(show: Show, reminder: Reminder) {
        appExecutors.diskIO().execute { dao.upsertReminder(show, reminder) }
    }

    fun deleteReminder(show: Show, reminder: Reminder) {
        appExecutors.diskIO().execute { dao.deleteReminder(show, reminder) }
    }
}