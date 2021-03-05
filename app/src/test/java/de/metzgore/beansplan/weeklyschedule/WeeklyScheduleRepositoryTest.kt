package de.metzgore.beansplan.weeklyschedule

import TestUtils
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.api.ApiSuccessResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.relations.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class WeeklyScheduleRepositoryTest {

    private val scheduleDao = mock<ScheduleRoomDao>()
    private val rbtvService = mock<RbtvScheduleApi>()
    private val clock = mock<Clock>()
    private val repo = WeeklyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors(), clock)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSchedule() {
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(LiveDataTestUtil
                .createFilledWeeklyScheduleWithDailySchedulesLiveData())

        repo.loadSchedule(false)
        verify(scheduleDao).getWeeklyScheduleWithDailySchedules()
    }

    @Test
    fun dontGoToNetwork() {
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(LiveDataTestUtil
                .createFilledWeeklyScheduleWithDailySchedulesLiveData())

        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()
    }


    @Test
    fun goToNetworkNotForcedWhenScheduleNull() {
        val dbData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(dbData)
        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)
        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()

        val nullSchedule = null
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(nullSchedule)
        dbData.value = null
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkNotForcedWhenScheduleEmpty() {
        val dbData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(dbData)
        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)
        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()

        val emptyScheduleLiveData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = emptyList()
        emptyScheduleLiveData.value = weeklyScheduleWithDailySchedules
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(emptyScheduleLiveData)
        dbData.value = null
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()
    }


    @Test
    fun goToNetworkForced() {
        val dbData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(dbData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        repo.loadSchedule(true).observeForever(observer)
        dbData.value = null
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkForcedWithEmptySchedule() {
        val dbData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(dbData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = emptyList()

        repo.loadSchedule(true).observeForever(observer)
        dbData.value = weeklyScheduleWithDailySchedules
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkForcedWithNotEmptySchedule() {
        val dbData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(dbData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = listOf(Date())

        repo.loadSchedule(true).observeForever(observer)
        dbData.value = weeklyScheduleWithDailySchedules
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()
    }

    @Test
    fun saveItemWhenCacheEmpty() {
        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val weeklyScheduleResponse = (LiveDataTestUtil.getValue(rbtvService.scheduleOfCurrentWeek()) as ApiSuccessResponse).body
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(LiveDataTestUtil
                .createFilledWeeklyScheduleWithDailySchedulesLiveData())

        //cache is null, save should be called once
        `when`(scheduleDao.getWeeklyScheduleResponse()).thenReturn(null)

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, times(1)).upsertSchedule(clock, weeklyScheduleResponse)
    }

    @Test
    fun dontSaveItemWhenCacheAndNetworkSame() {
        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(call)
        val weeklyScheduleResponse = (LiveDataTestUtil.getValue(rbtvService.scheduleOfCurrentWeek()) as ApiSuccessResponse).body
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(LiveDataTestUtil
                .createFilledWeeklyScheduleWithDailySchedulesLiveData())

        //cache and network are same, don't call save
        `when`(scheduleDao.getWeeklyScheduleResponse()).thenReturn(weeklyScheduleResponse)

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, never()).upsertSchedule(clock, weeklyScheduleResponse)
    }

    @Test
    fun saveItemWhenCacheAndNetworkAreDifferent() {
        val updatedSchedule = TestUtils.createWeeklyScheduleOneWeek()
        val updatedCall = ApiUtil.successCall(updatedSchedule)

        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(updatedCall)
        val weeklyScheduleResponse = (LiveDataTestUtil.getValue(rbtvService.scheduleOfCurrentWeek()) as ApiSuccessResponse).body
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()

        `when`(scheduleDao.getWeeklyScheduleWithDailySchedules()).thenReturn(LiveDataTestUtil
                .createFilledWeeklyScheduleWithDailySchedulesLiveData())

        //cache and network are different, save should be called once
        `when`(scheduleDao.getWeeklyScheduleResponse()).thenReturn(WeeklyScheduleResponse())

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, times(1)).upsertSchedule(clock, weeklyScheduleResponse)
    }
}