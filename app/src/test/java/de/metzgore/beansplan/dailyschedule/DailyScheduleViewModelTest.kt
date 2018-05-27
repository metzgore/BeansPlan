package de.metzgore.beansplan.dailyschedule

import TestUtils
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.mock
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class DailyScheduleViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dailyScheduleRepository = mock(DailyScheduleRepository::class.java)
    private val dailyScheduleViewModel = DailyScheduleViewModel(dailyScheduleRepository)

    @Test
    fun testNull() {
        assertThat(dailyScheduleViewModel.schedule, notNullValue())
        assertThat(dailyScheduleViewModel.schedule.value, nullValue())
        assertThat(dailyScheduleViewModel.isEmpty, notNullValue())
        assertThat(dailyScheduleViewModel.isEmpty.value, nullValue())
        assertThat(dailyScheduleViewModel.isLoading, notNullValue())
        assertThat(dailyScheduleViewModel.isLoading.value, nullValue())
        verify(dailyScheduleRepository, never()).loadSchedule(anyBoolean())
    }

    @Test
    fun testLoadDailySchedule() {
        dailyScheduleViewModel.schedule.observeForever(mock())
        dailyScheduleViewModel.isEmpty.observeForever(mock())

        `when`(dailyScheduleRepository.loadSchedule(true)).thenReturn(LiveDataTestUtil.createFilledDailyScheduleLiveData(Status.SUCCESS, true))

        //test loading from network with empty schedule
        dailyScheduleViewModel.loadSchedule()
        verify(dailyScheduleRepository, never()).loadSchedule(false)
        verify(dailyScheduleRepository, times(1)).loadSchedule(true)

        //test loading from cache
        dailyScheduleViewModel.loadSchedule()
        verify(dailyScheduleRepository, times(1)).loadSchedule(true)
        verify(dailyScheduleRepository, times(1)).loadSchedule(false)

        //test force loading from network with schedule
        dailyScheduleViewModel.loadScheduleFromNetwork()
        verify(dailyScheduleRepository, times(2)).loadSchedule(true)
        verify(dailyScheduleRepository, times(1)).loadSchedule(false)

        verifyNoMoreInteractions(dailyScheduleRepository)
    }

    @Test
    fun testSetDailySchedule() {
        dailyScheduleViewModel.schedule.observeForever(mock())

        dailyScheduleViewModel.setSchedule(TestUtils.createDailySchedule())
        verify(dailyScheduleRepository, never()).loadSchedule(anyBoolean())

        verifyNoMoreInteractions(dailyScheduleRepository)
    }

    @Test
    fun sendResultToUI() {
        val foo = MutableLiveData<Resource<DailySchedule>>()
        val bar = MutableLiveData<Resource<DailySchedule>>()

        `when`(dailyScheduleRepository.loadSchedule(true)).thenReturn(foo)
        val observer = mock<Observer<Resource<DailySchedule>>>()
        dailyScheduleViewModel.schedule.observeForever(observer)
        dailyScheduleViewModel.loadScheduleFromNetwork()

        verify(observer, never()).onChanged(any())
        val fooValue = Resource.success(DailySchedule(), true)

        foo.value = fooValue
        verify(observer).onChanged(fooValue)
        reset(observer)

        `when`(dailyScheduleRepository.loadSchedule(true)).thenReturn(bar)
        dailyScheduleViewModel.loadScheduleFromNetwork()

        verify(observer, never()).onChanged(any())
        val barValue = Resource.success(TestUtils.createDailySchedule(), false)

        bar.value = barValue
        verify(observer).onChanged(barValue)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testEmptySchedule() {
        dailyScheduleViewModel.schedule.observeForever(mock())
        dailyScheduleViewModel.isEmpty.observeForever(mock())

        //test loading from network with filled schedule
        `when`(dailyScheduleRepository.loadSchedule(anyBoolean())).thenReturn(LiveDataTestUtil.createFilledDailyScheduleLiveData(Status.SUCCESS, true))
        dailyScheduleViewModel.loadSchedule()
        assertThat(dailyScheduleViewModel.isEmpty.value, `is`(false))

        //test loading from network with empty schedule
        `when`(dailyScheduleRepository.loadSchedule(anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyDailyScheduleLiveData(Status.SUCCESS, true))
        dailyScheduleViewModel.loadSchedule()
        assertThat(dailyScheduleViewModel.isEmpty.value, `is`(true))
    }

    @Test
    fun testLoadingState() {
        dailyScheduleViewModel.schedule.observeForever(mock())
        dailyScheduleViewModel.isLoading.observeForever(mock())

        //test loading with state LOADING
        `when`(dailyScheduleRepository.loadSchedule(anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyDailyScheduleLiveData(Status.LOADING, true))
        dailyScheduleViewModel.loadSchedule()
        assertThat(dailyScheduleViewModel.isLoading.value, `is`(true))

        //test loading with state SUCCESS
        `when`(dailyScheduleRepository.loadSchedule(anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyDailyScheduleLiveData(Status.SUCCESS, true))
        dailyScheduleViewModel.loadSchedule()
        assertThat(dailyScheduleViewModel.isLoading.value, `is`(false))

        //test loading with state ERROR
        `when`(dailyScheduleRepository.loadSchedule(anyBoolean())).thenReturn(LiveDataTestUtil.createEmptyDailyScheduleLiveData(Status.ERROR, true))
        dailyScheduleViewModel.loadSchedule()
        assertThat(dailyScheduleViewModel.isLoading.value, `is`(false))
    }
}