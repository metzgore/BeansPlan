package de.metzgore.beansplan.weeklyschedule

import TestUtils
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.api.ApiSuccessResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

class WeeklyScheduleRepositoryTest {

    private val scheduleDao = mock(WeeklyScheduleDao::class.java)
    private val rbtvService = mock(RbtvScheduleApi::class.java)
    private val clock = mock(Clock::class.java)
    private val repo = WeeklyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors(), clock)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSchedule() {
        repo.loadScheduleFromCache(false)
        verify(scheduleDao).get()
    }

    @Test
    fun dontGoToNetwork() {
        val cacheData = TestUtils.createWeeklySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleResponse>>>()

        repo.loadScheduleFromCache(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkNotForced() {
        val cacheData = TestUtils.createWeeklySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleResponse>>>()

        repo.loadScheduleFromCache(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadScheduleFromCache(false).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(WeeklyScheduleResponse())
        repo.loadScheduleFromCache(false).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkForced() {
        val cacheData = TestUtils.createWeeklySchedule()
        Mockito.`when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklyScheduleResponse>>>()

        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(WeeklyScheduleResponse())
        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(rbtvService, times(3)).scheduleOfCurrentWeek()
    }

    @Test
    fun saveItemWhenCacheAndNetworkAreDifferent() {
        `when`(clock.nowInMillis()).thenReturn(1)

        val schedule = WeeklyScheduleResponse()
        val call = ApiUtil.successCall(schedule)

        val updatedSchedule = TestUtils.createWeeklySchedule()
        val updatedCall = ApiUtil.successCall(updatedSchedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val dailySchedule = (LiveDataTestUtil.getValue(rbtvService.scheduleOfCurrentWeek()) as ApiSuccessResponse).body
        val observer = mock<Observer<Resource<WeeklyScheduleResponse>>>()
        val argument = ArgumentCaptor.forClass(WeeklyScheduleResponse::class.java)

        //cache is null, save should be called once
        `when`(scheduleDao!!.get()).thenReturn(null)

        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(scheduleDao, times(1)).save(argument.capture())
        MatcherAssert.assertThat(argument.value, CoreMatchers.`is`(dailySchedule))
        MatcherAssert.assertThat(argument.value.timestamp, CoreMatchers.`is`(1L))

        //data in cache is the same as data from network, save should still be only called once
        `when`(scheduleDao.get()).thenReturn(dailySchedule)

        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(scheduleDao, times(1)).save(argument.capture())
        MatcherAssert.assertThat(argument.value, CoreMatchers.`is`(dailySchedule))
        MatcherAssert.assertThat(argument.value.timestamp, CoreMatchers.`is`(1L))

        //data in cache is different from the data from network, save should be called second time
        `when`(clock.nowInMillis()).thenReturn(2)
        `when`(rbtvService.scheduleOfCurrentWeek()).thenReturn(updatedCall)
        `when`(scheduleDao.get()).thenReturn(dailySchedule)

        repo.loadScheduleFromCache(true).observeForever(observer)
        verify(scheduleDao, times(2)).save(argument.capture())
        MatcherAssert.assertThat(argument.value, CoreMatchers.`is`(updatedSchedule))
        MatcherAssert.assertThat(argument.value.timestamp, CoreMatchers.`is`(2L))
    }
}