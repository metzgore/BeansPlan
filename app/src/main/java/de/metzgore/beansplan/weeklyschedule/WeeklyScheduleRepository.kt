package de.metzgore.beansplan.weeklyschedule

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.api.ApiResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.NetworkBoundResource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class WeeklyScheduleRepository @Inject constructor(private val api: RbtvScheduleApi, private val dao: WeeklyScheduleDao,
                                                   private val roomDao: WeeklyScheduleRoomDao,
                                                   private val
                                                   appExecutors: AppExecutors, private val
                                                   clock: Clock) : ScheduleRepository<WeeklyScheduleWithDailySchedules> {

    val weeklySchedule = roomDao.getWeeklyScheduleWithDailySchedulesDistinct()

    override fun loadScheduleFromCache(date: Date): LiveData<WeeklyScheduleWithDailySchedules> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadSchedule(forceRefresh: Boolean):
            LiveData<Resource<WeeklyScheduleWithDailySchedules>> {
        return object : NetworkBoundResource<WeeklyScheduleWithDailySchedules, WeeklyScheduleResponse>(appExecutors,
                forceRefresh) {
            override fun shouldSave(item: WeeklyScheduleResponse): Boolean {
                val savedSchedule = roomDao.getWeeklyScheduleResponse()
                return savedSchedule != item
            }

            override fun saveCallResult(item: WeeklyScheduleResponse) {
                //TODO transaction
                val weeklySchedule = de.metzgore.beansplan.data.room.WeeklySchedule(timestamp =
                clock.nowInMillis(), weeklyScheduleRaw = item)
                roomDao.upsert(weeklySchedule)
                val dailySchedules = arrayListOf<de.metzgore.beansplan.data.room.DailySchedule>()
                item.dateKeys.forEach { date ->
                    dailySchedules.add(de.metzgore
                            .beansplan.data.room.DailySchedule(date, weeklySchedule.id))
                }
                roomDao.upsertDailySchedules(dailySchedules)
                roomDao.deleteLeftOverDailySchedule(dailySchedules.map {
                    it.id.time
                })

                item.schedule.forEach { (date, shows) ->
                    val showsRoom = arrayListOf<de.metzgore.beansplan.data.room.Show>()
                    shows.forEach { show ->
                        showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                                show.timeEnd, show.length, show.game, show.youtubeId, show.type))
                    }
                    roomDao.upsertShows(showsRoom)
                }
            }

            override fun shouldFetch(data: WeeklyScheduleWithDailySchedules?): Boolean {
                return forceRefresh || data == null || data.dailySchedulesWithShows.isEmpty()
            }

            override fun loadFromDb(): LiveData<WeeklyScheduleWithDailySchedules> {
                return weeklySchedule
            }

            override fun createCall(): LiveData<ApiResponse<WeeklyScheduleResponse>> {
                return api.scheduleOfCurrentWeek()
            }
        }.asLiveData()
    }
}