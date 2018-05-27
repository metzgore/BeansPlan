package de.metzgore.beansplan.dailyschedule

import TestUtils
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.api.ApiSuccessResponse
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.shared.DailyScheduleDao
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.*


class DailyScheduleRepositoryTest {

    private val scheduleDao = mock(DailyScheduleDao::class.java)
    private val rbtvService = mock(RbtvScheduleApi::class.java)
    private val clock = mock(Clock::class.java)
    private val repo = DailyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors(), clock)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSchedule() {
        repo.loadSchedule(false)
        verify(scheduleDao).get()
    }

    @Test
    fun dontGoToNetwork() {
        val cacheData = TestUtils.createDailySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = DailySchedule()
        val call = ApiUtil.successCall(schedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        `when`(rbtvService!!.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())).thenReturn(call)
        val observer = mock<Observer<Resource<DailySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())
    }

    @Test
    fun goToNetworkNotForced() {
        val cacheData = TestUtils.createDailySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = DailySchedule()
        val call = ApiUtil.successCall(schedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        `when`(rbtvService!!.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())).thenReturn(call)
        val observer = mock<Observer<Resource<DailySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())

        `when`(scheduleDao.get()).thenReturn(DailySchedule())
        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())
    }

    @Test
    fun goToNetworkForced() {
        val cacheData = TestUtils.createDailySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = DailySchedule()
        val call = ApiUtil.successCall(schedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        `when`(rbtvService!!.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())).thenReturn(call)
        val observer = mock<Observer<Resource<DailySchedule>>>()

        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())

        `when`(scheduleDao.get()).thenReturn(DailySchedule())
        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(3)).scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())
    }

    @Test
    fun saveItemWhenCacheAndNetworkAreDifferent() {
        `when`(clock.nowInMillis()).thenReturn(1)

        val schedule = DailySchedule()
        val call = ApiUtil.successCall(schedule)

        val updatedSchedule = TestUtils.createDailySchedule()
        val updatedCall = ApiUtil.successCall(updatedSchedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        `when`(rbtvService!!.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())).thenReturn(call)
        val dailySchedule = (LiveDataTestUtil.getValue(rbtvService.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())) as ApiSuccessResponse).body
        val observer = mock<Observer<Resource<DailySchedule>>>()
        val argument = ArgumentCaptor.forClass(DailySchedule::class.java)

        //cache is null, save should be called once
        `when`(scheduleDao!!.get()).thenReturn(null)

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, times(1)).save(argument.capture())
        assertThat(argument.value, `is`(dailySchedule))
        assertThat(argument.value.timestamp, `is`(1L))

        //data in cache is the same as data from network, save should still be only called once
        `when`(scheduleDao.get()).thenReturn(dailySchedule)

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, times(1)).save(argument.capture())
        assertThat(argument.value, `is`(dailySchedule))
        assertThat(argument.value.timestamp, `is`(1L))

        //data in cache is different from the data from network, save should be called second time
        `when`(clock.nowInMillis()).thenReturn(2)
        `when`(rbtvService.scheduleOfDay(year, month.formatDoubleDigit(), day.formatDoubleDigit())).thenReturn(updatedCall)
        `when`(scheduleDao.get()).thenReturn(dailySchedule)

        repo.loadSchedule(true).observeForever(observer)
        verify(scheduleDao, times(2)).save(argument.capture())
        assertThat(argument.value, `is`(updatedSchedule))
        assertThat(argument.value.timestamp, `is`(2L))
    }

    private fun Int.formatDoubleDigit(): String = String.format(Locale.GERMANY, "%02d", this)
}