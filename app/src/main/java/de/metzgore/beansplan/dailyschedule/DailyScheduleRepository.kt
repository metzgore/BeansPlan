package de.metzgore.beansplan.dailyschedule

import androidx.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
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

    fun upsertReminder(showWithReminder: ShowWithReminder) {
        appExecutors.diskIO().execute { dao.upsertReminder(showWithReminder) }
    }

    fun deleteReminder(showWithReminder: ShowWithReminder) {
        appExecutors.diskIO().execute { dao.deleteReminder(showWithReminder) }
    }
}