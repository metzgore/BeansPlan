package de.metzgore.beansplan.weeklyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.di.Injector
import de.metzgore.beansplan.utils.InstantAppExecutors
import de.metzgore.beansplan.utils.mock
import okio.Okio
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class WeeklyScheduleRepositoryTest {

    private val scheduleDao = Mockito.mock(WeeklyScheduleDao::class.java)
    private val rbtvService = Mockito.mock(RbtvScheduleApi::class.java)
    private val repo = WeeklyScheduleRepository(rbtvService, scheduleDao, InstantAppExecutors())

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSchedule() {
        repo.loadSchedule(false)
        Mockito.verify(scheduleDao).get()
    }

    @Test
    fun dontGoToNetwork() {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), WeeklySchedule::class.java)
        Mockito.`when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        Mockito.`when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.never()).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkNotForced() {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), WeeklySchedule::class.java)
        Mockito.`when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        Mockito.`when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(false).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.never()).scheduleOfCurrentWeek()

        Mockito.`when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(false).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.times(1)).scheduleOfCurrentWeek()

        Mockito.`when`(scheduleDao.get()).thenReturn(WeeklySchedule())
        repo.loadSchedule(false).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.times(2)).scheduleOfCurrentWeek()
    }

    @Test
    fun goToNetworkForced() {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule.json")
        val source = Okio.buffer(Okio.source(inputStream))

        val cacheData = Injector.provideGson().fromJson(source.readUtf8(), WeeklySchedule::class.java)
        Mockito.`when`(scheduleDao!!.get()).thenReturn(cacheData)

        val schedule = WeeklySchedule()
        val call = ApiUtil.successCall(schedule)

        val calendar = GregorianCalendar()
        calendar.time = Date()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        Mockito.`when`(rbtvService!!.scheduleOfCurrentWeek()).thenReturn(call)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()

        repo.loadSchedule(true).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.times(1)).scheduleOfCurrentWeek()

        Mockito.`when`(scheduleDao.get()).thenReturn(null)
        repo.loadSchedule(true).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.times(2)).scheduleOfCurrentWeek()

        Mockito.`when`(scheduleDao.get()).thenReturn(WeeklySchedule())
        repo.loadSchedule(true).observeForever(observer)
        Mockito.verify(rbtvService, Mockito.times(3)).scheduleOfCurrentWeek()
    }
}