package de.metzgore.beansplan.dailyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.room.DailyScheduleWithShows
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRoomDao
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DailyScheduleRepository @Inject constructor(private val dao: WeeklyScheduleRoomDao) :
        ScheduleRepository<DailyScheduleWithShows> {

    val dailySchedules = hashMapOf<Date, LiveData<DailyScheduleWithShows>>()
    lateinit var loadedDate: Date

    override fun loadScheduleFromCache(date: Date): LiveData<DailyScheduleWithShows> {
        if (!::loadedDate.isInitialized || loadedDate != date) {
            loadedDate = date

            if (!dailySchedules.containsKey(loadedDate))
                dailySchedules[loadedDate] = dao.getDailyScheduleWithShowsDistinct(date)

        }
        return dao.getDailyScheduleWithShowsDistinct(date)
    }

    override fun loadSchedule(forceRefresh: Boolean): LiveData<Resource<DailyScheduleWithShows>> {
        TODO(reason = "not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}