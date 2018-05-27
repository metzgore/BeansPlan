package de.metzgore.beansplan.weeklyschedule

import TestUtils
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.mock
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class WeeklyScheduleViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val weeklyScheduleRepository = Mockito.mock(WeeklyScheduleRepository::class.java)
    private val weeklyScheduleViewModel = WeeklyScheduleViewModel(weeklyScheduleRepository)

    @Test
    fun testNull() {
        assertThat(weeklyScheduleViewModel.schedule, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.schedule.value, CoreMatchers.nullValue())
        assertThat(weeklyScheduleViewModel.isEmpty, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.isEmpty.value, CoreMatchers.nullValue())
        assertThat(weeklyScheduleViewModel.isLoading, CoreMatchers.notNullValue())
        assertThat(weeklyScheduleViewModel.isLoading.value, CoreMatchers.nullValue())
        Mockito.verify(weeklyScheduleRepository, Mockito.never()).loadSchedule(Mockito.anyBoolean())
    }

    @Test
    fun testLoadWeeklySchedule() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isEmpty.observeForever(mock())

        Mockito.`when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(LiveDataTestUtil.createFilledWeeklyScheduleLiveData(Status.SUCCESS, true))

        //test loading from network with empty schedule
        weeklyScheduleViewModel.loadSchedule()
        Mockito.verify(weeklyScheduleRepository, Mockito.never()).loadSchedule(false)
        Mockito.verify(weeklyScheduleRepository, Mockito.times(1)).loadSchedule(true)

        //test loading from cache
        weeklyScheduleViewModel.loadSchedule()
        Mockito.verify(weeklyScheduleRepository, Mockito.times(1)).loadSchedule(true)
        Mockito.verify(weeklyScheduleRepository, Mockito.times(1)).loadSchedule(false)

        //test force loading from network with schedule
        weeklyScheduleViewModel.loadScheduleFromNetwork()
        Mockito.verify(weeklyScheduleRepository, Mockito.times(2)).loadSchedule(true)
        Mockito.verify(weeklyScheduleRepository, Mockito.times(1)).loadSchedule(false)

        Mockito.verifyNoMoreInteractions(weeklyScheduleRepository)
    }

    @Test
    fun sendResultToUI() {
        val foo = MutableLiveData<Resource<WeeklySchedule>>()
        val bar = MutableLiveData<Resource<WeeklySchedule>>()

        Mockito.`when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(foo)
        val observer = mock<Observer<Resource<WeeklySchedule>>>()
        weeklyScheduleViewModel.schedule.observeForever(observer)
        weeklyScheduleViewModel.loadScheduleFromNetwork()

        Mockito.verify(observer, Mockito.never()).onChanged(Mockito.any())
        val fooValue = Resource.success(WeeklySchedule(), true)

        foo.value = fooValue
        Mockito.verify(observer).onChanged(fooValue)
        Mockito.reset(observer)

        Mockito.`when`(weeklyScheduleRepository.loadSchedule(true)).thenReturn(bar)
        weeklyScheduleViewModel.loadScheduleFromNetwork()

        Mockito.verify(observer, Mockito.never()).onChanged(Mockito.any())
        val barValue = Resource.success(TestUtils.createWeeklySchedule(), false)

        bar.value = barValue
        Mockito.verify(observer).onChanged(barValue)
        Mockito.verifyNoMoreInteractions(observer)
    }

    @Test
    fun testEmptySchedule() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isEmpty.observeForever(mock())

        //test loading from network with filled schedule
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean())).thenReturn(LiveDataTestUtil.createFilledWeeklyScheduleLiveData(Status.SUCCESS, true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isEmpty.value, CoreMatchers.`is`(false))

        //test loading from network with empty schedule
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleLiveData(Status.SUCCESS, true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isEmpty.value, CoreMatchers.`is`(true))
    }

    @Test
    fun testLoadingState() {
        weeklyScheduleViewModel.schedule.observeForever(mock())
        weeklyScheduleViewModel.isLoading.observeForever(mock())

        //test loading with state LOADING
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleLiveData(Status.LOADING, true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, CoreMatchers.`is`(true))

        //test loading with state SUCCESS
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleLiveData(Status.SUCCESS, true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, CoreMatchers.`is`(false))

        //test loading with state ERROR
        Mockito.`when`(weeklyScheduleRepository.loadSchedule(Mockito.anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyWeeklyScheduleLiveData(Status.ERROR, true))
        weeklyScheduleViewModel.loadSchedule()
        assertThat(weeklyScheduleViewModel.isLoading.value, CoreMatchers.`is`(false))
    }
}