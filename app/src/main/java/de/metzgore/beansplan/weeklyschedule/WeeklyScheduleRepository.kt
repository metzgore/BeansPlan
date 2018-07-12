package de.metzgore.beansplan.weeklyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.api.ApiResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.NetworkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class WeeklyScheduleRepository @Inject constructor(private val api: RbtvScheduleApi,
                                                   private val roomDao: ScheduleRoomDao,
                                                   private val
                                                   appExecutors: AppExecutors, private val
                                                   clock: Clock) {

    lateinit var weeklySchedule: LiveData<WeeklyScheduleWithDailySchedules>

    fun loadSchedule(forceRefresh: Boolean):
            LiveData<Resource<WeeklyScheduleWithDailySchedules>> {
        return object : NetworkBoundResource<WeeklyScheduleWithDailySchedules, WeeklyScheduleResponse>(appExecutors,
                forceRefresh) {
            override fun shouldSave(item: WeeklyScheduleResponse): Boolean {
                val savedSchedule = roomDao.getWeeklyScheduleResponse()
                return savedSchedule != item
            }

            override fun saveCallResult(item: WeeklyScheduleResponse) {
                roomDao.upsertSchedule(clock, item)
            }

            override fun shouldFetch(data: WeeklyScheduleWithDailySchedules?): Boolean {
                return forceRefresh || data == null || data.dailySchedulesWithShows.isEmpty()
            }

            override fun loadFromDb(): LiveData<WeeklyScheduleWithDailySchedules> {
                if (!::weeklySchedule.isInitialized) {
                    weeklySchedule = roomDao.getWeeklyScheduleWithDailySchedulesDistinct()
                }
                return weeklySchedule
            }

            override fun createCall(): LiveData<ApiResponse<WeeklyScheduleResponse>> {
                return api.scheduleOfCurrentWeek()
            }
        }.asLiveData()
    }
}