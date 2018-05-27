package de.metzgore.beansplan.dailyschedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.api.ApiResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.shared.DailyScheduleDao
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.NetworkBoundResource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DailyScheduleRepository @Inject constructor(private val api: RbtvScheduleApi, private val dao: DailyScheduleDao, private val
appExecutors: AppExecutors, private val clock: Clock) : ScheduleRepository<DailySchedule> {

    private val scheduleCacheData = MutableLiveData<DailySchedule>()

    override fun loadSchedule(forceRefresh: Boolean): LiveData<Resource<DailySchedule>> {
        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return loadScheduleOfDay(forceRefresh, year, month, day)
    }

    private fun loadScheduleOfDay(forceRefresh: Boolean, year: Int,
                                  month: Int, day: Int): LiveData<Resource<DailySchedule>> {
        val formattedDay = day.formatDoubleDigit()
        val formattedMonth = month.formatDoubleDigit()

        return object : NetworkBoundResource<DailySchedule, DailySchedule>(appExecutors, forceRefresh) {
            override fun shouldSave(item: DailySchedule): Boolean {
                val savedSchedule = dao.get()
                return savedSchedule != item
            }

            override fun saveCallResult(item: DailySchedule) {
                //TODO check preSerialize
                item.timestamp = clock.nowInMillis()
                dao.save(item)
            }

            override fun shouldFetch(data: DailySchedule?): Boolean {
                return forceRefresh || data == null || data.isEmpty
            }

            override fun loadFromDb(): LiveData<DailySchedule> {
                scheduleCacheData.value = dao.get()
                return scheduleCacheData
            }

            override fun createCall(): LiveData<ApiResponse<DailySchedule>> {
                return api.scheduleOfDay(year, formattedMonth, formattedDay)
            }
        }.asLiveData()
    }

    private fun Int.formatDoubleDigit(): String = String.format(Locale.GERMANY, "%02d", this)
}