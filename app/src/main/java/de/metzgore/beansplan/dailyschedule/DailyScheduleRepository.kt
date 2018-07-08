package de.metzgore.beansplan.dailyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.data.room.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.testing.OpenForTesting
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DailyScheduleRepository @Inject constructor(private val dao: ScheduleRoomDao) {

    fun loadScheduleFromCache(date: Date): LiveData<DailyScheduleWithShows> {
        return dao.getDailyScheduleWithShowsDistinct(date)
    }
}