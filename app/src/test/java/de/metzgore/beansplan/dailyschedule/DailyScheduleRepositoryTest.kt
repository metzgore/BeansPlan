package de.metzgore.beansplan.dailyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.shared.DailyScheduleDao
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.di.Injector
import de.metzgore.beansplan.utils.InstantAppExecutors
import de.metzgore.beansplan.utils.mock
import okio.Okio
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class DailyScheduleRepositoryTest {

    private val scheduleDao = mock(DailyScheduleDao::class.java)
    private val rbtvService = mock(RbtvScheduleApi::class.java)
    private val repo = DailyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors())

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
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/daily_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), DailySchedule::class.java)
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
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/daily_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), DailySchedule::class.java)
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
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/daily_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), DailySchedule::class.java)
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

    private fun Int.formatDoubleDigit(): String = String.format(Locale.GERMANY, "%02d", this)
}