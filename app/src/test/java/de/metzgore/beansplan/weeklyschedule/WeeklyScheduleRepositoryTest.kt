package de.metzgore.beansplan.weeklyschedule

import TestUtils
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.utils.InstantAppExecutors
import de.metzgore.beansplan.mock
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class WeeklyScheduleRepositoryTest {

    private val scheduleDao = mock(WeeklyScheduleDao::class.java)
    private val rbtvService = mock(RbtvScheduleApi::class.java)
    private val repo = WeeklyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors())

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
        val cacheData = TestUtils.createWeeklySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkNotForced() {
        val cacheData = TestUtils.createWeeklySchedule()
        `when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, never()).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(WeeklySchedule())
        repo.loadSchedule(false).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkForced() {
        val cacheData = TestUtils.createWeeklySchedule()
        Mockito.`when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        `when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(1)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(2)).scheduleOfCurrentWeek()

        `when`(scheduleDao.get()).thenReturn(WeeklySchedule())
        repo.loadSchedule(true).observeForever(observer)
        verify(rbtvService, times(3)).scheduleOfCurrentWeek()
    }
}