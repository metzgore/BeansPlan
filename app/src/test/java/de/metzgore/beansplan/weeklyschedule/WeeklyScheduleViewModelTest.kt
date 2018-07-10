package de.metzgore.beansplan.weeklyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.mock
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

@RunWith(JUnit4::class)
class WeeklyScheduleViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val weeklyScheduleRepository = mock<WeeklyScheduleRepository>()
    private val weeklyScheduleViewModel = WeeklyScheduleViewModel(weeklyScheduleRepository)

    @Test
    fun testNull() {
        assertThat(weeklyScheduleViewModel.schedule, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.schedule.value, CoreMatchers.nullValue())
        assertThat(weeklyScheduleViewModel.isEmpty, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.isEmpty.value, CoreMatchers.nullValue())
        assertThat(weeklyScheduleViewModel.isLoading, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.isLoading.value, CoreMatchers.nullValue())
        verify(weeklyScheduleRepository, never()).loadSchedule(Mockito
                .anyBoolean())
    }

    @Test
    fun testLoadWeeklySchedule() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isEmpty.observeForever(mock())

        `when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(LiveDataTestUtil.createFilledWeeklyScheduleWithDailySchedulesResourceLiveData(Status.SUCCESS, true))

        //test loading from network with empty schedule
        weeklyScheduleViewModel.loadSchedule()
        verify(weeklyScheduleRepository, never()).loadSchedule(false)
        verify(weeklyScheduleRepository, times(1)).loadSchedule(true)

        //test loading from cache
        weeklyScheduleViewModel.loadSchedule()
        verify(weeklyScheduleRepository, times(1)).loadSchedule(true)
        verify(weeklyScheduleRepository, times(1)).loadSchedule(false)

        //test force loading from network with schedule
        weeklyScheduleViewModel.loadScheduleFromNetwork()
        verify(weeklyScheduleRepository, times(2)).loadSchedule(true)
        verify(weeklyScheduleRepository, times(1)).loadSchedule(false)

        Mockito.verifyNoMoreInteractions(weeklyScheduleRepository)
    }

    @Test
    fun sendResultToUI() {
        val foo = MutableLiveData<Resource<WeeklyScheduleWithDailySchedules>>()
        val bar = MutableLiveData<Resource<WeeklyScheduleWithDailySchedules>>()

        `when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(foo)
        val observer = mock<Observer<Resource<WeeklyScheduleWithDailySchedules>>>()
        weeklyScheduleViewModel.schedule.observeForever(observer)
        weeklyScheduleViewModel.loadScheduleFromNetwork()

        verify(observer, never()).onChanged(any())

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = listOf(Date())

        val fooValue = Resource.success(weeklyScheduleWithDailySchedules, true)

        foo.value = fooValue
        verify(observer).onChanged(fooValue)
        reset(observer)

        `when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(bar)
        weeklyScheduleViewModel.loadScheduleFromNetwork()

        verify(observer, never()).onChanged(any())
        val barValue = Resource.success(WeeklyScheduleWithDailySchedules(), false)

        bar.value = barValue
        verify(observer).onChanged(barValue)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testEmptySchedule() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isEmpty.observeForever(mock())

        //test loading from network with filled schedule
        `when`(weeklyScheduleRepository.loadSchedule(anyBoolean()))
                .thenReturn(LiveDataTestUtil.createFilledWeeklyScheduleWithDailySchedulesResourceLiveData(Status.SUCCESS,
                        true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isEmpty.value, `is`(false))

        //test loading from network with empty schedule
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean()))
                .thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleWithDailySchedulesResourceLiveData(Status.SUCCESS,
                        true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isEmpty.value, `is`(true))
    }

    @Test
    fun testLoadingState() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isLoading.observeForever(mock())

        //test loading with state LOADING
        `when`(weeklyScheduleRepository.loadSchedule(anyBoolean()))
                .thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleWithDailySchedulesResourceLiveData(Status.LOADING,
                        true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, `is`(true))

        //test loading with state SUCCESS
        `when`(weeklyScheduleRepository.loadSchedule(anyBoolean()))
                .thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleWithDailySchedulesResourceLiveData(Status.SUCCESS,
                        true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, `is`(false))

        //test loading with state ERROR
        `when`(weeklyScheduleRepository.loadSchedule(anyBoolean()))
                .thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleWithDailySchedulesResourceLiveData(Status.ERROR,
                        true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, `is`(false))
    }
}