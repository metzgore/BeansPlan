package de.metzgore.beansplan.weeklyschedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.api.ApiResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.NetworkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class WeeklyScheduleRepository @Inject constructor(private val api: RbtvScheduleApi, private val dao: WeeklyScheduleDao,
                                                   private val
                                                   appExecutors: AppExecutors, private val
                                                   clock: Clock) : ScheduleRepository<WeeklySchedule> {

    private val weeklyScheduleCacheData = MutableLiveData<WeeklySchedule>()

    override fun loadSchedule(forceRefresh: Boolean): LiveData<Resource<WeeklySchedule>> {
        return object : NetworkBoundResource<WeeklySchedule, WeeklySchedule>(appExecutors,
                forceRefresh) {
            override fun shouldSave(item: WeeklySchedule): Boolean {
                val savedSchedule = dao.get()
                return savedSchedule != item
            }

            override fun saveCallResult(item: WeeklySchedule) {
                //TODO check preSerialize
                item.timestamp = clock.nowInMillis()
                dao.save(item)
            }

            override fun shouldFetch(data: WeeklySchedule?): Boolean {
                return forceRefresh || data == null || data.isEmpty
            }

            override fun loadFromDb(): LiveData<WeeklySchedule> {
                weeklyScheduleCacheData.value = dao.get()
                return weeklyScheduleCacheData
            }

            override fun createCall(): LiveData<ApiResponse<WeeklySchedule>> {
                return api.scheduleOfCurrentWeek()
            }
        }.asLiveData()
    }
}