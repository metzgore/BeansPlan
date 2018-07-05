package de.metzgore.beansplan.dailyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.data.room.DailyScheduleWithShows
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DailyScheduleRepository @Inject constructor(private val dao: ScheduleRoomDao) {

    val dailySchedules = hashMapOf<Date, LiveData<DailyScheduleWithShows>>()
    lateinit var loadedDate: Date

    fun loadScheduleFromCache(date: Date): LiveData<DailyScheduleWithShows> {
        if (!::loadedDate.isInitialized || loadedDate != date) {
            loadedDate = date

            if (!dailySchedules.containsKey(loadedDate))
                dailySchedules[loadedDate] = dao.getDailyScheduleWithShowsDistinct(date)

        }
        return dao.getDailyScheduleWithShowsDistinct(date)
    }
}