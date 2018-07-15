package de.metzgore.beansplan.dailyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.mock
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.util.*

@RunWith(JUnit4::class)
class DailyScheduleViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dailyScheduleRepository = mock<DailyScheduleRepository>()
    private val dailyScheduleViewModel = DailyScheduleViewModel(dailyScheduleRepository)

    @Test
    fun testNull() {
        assertThat(dailyScheduleViewModel.schedule, notNullValue())
        assertThat(dailyScheduleViewModel.schedule.value, nullValue())
        assertThat(dailyScheduleViewModel.isEmpty, notNullValue())
        assertThat(dailyScheduleViewModel.isEmpty.value, nullValue())
        verify(dailyScheduleRepository, never()).loadScheduleFromCache(Date())
    }

    @Test
    fun testLoadDailySchedule() {
        dailyScheduleViewModel.schedule.observeForever(mock())
        dailyScheduleViewModel.isEmpty.observeForever(mock())

        val date = Date()

        `when`(dailyScheduleRepository.loadScheduleFromCache(date)).thenReturn(LiveDataTestUtil
                .createFilledDailyScheduleWithShowsLiveData())

        //test loading from network with empty schedule
        dailyScheduleViewModel.loadSchedule(date)
        verify(dailyScheduleRepository, times(1)).loadScheduleFromCache(date)

        verifyNoMoreInteractions(dailyScheduleRepository)
    }

    @Test
    fun sendResultToUI() {
        val foo = MutableLiveData<DailyScheduleWithShows>()
        val bar = MutableLiveData<DailyScheduleWithShows>()

        val date = Date()

        `when`(dailyScheduleRepository.loadScheduleFromCache(date)).thenReturn(foo)
        val observer = mock<Observer<DailyScheduleWithShows>>()
        dailyScheduleViewModel.schedule.observeForever(observer)
        dailyScheduleViewModel.loadSchedule(date)

        verify(observer, never()).onChanged(any())
        val fooValue = DailyScheduleWithShows()

        foo.value = fooValue
        verify(observer).onChanged(fooValue)
        reset(observer)

        `when`(dailyScheduleRepository.loadScheduleFromCache(date)).thenReturn(bar)
        dailyScheduleViewModel.loadSchedule(date)

        verify(observer, never()).onChanged(any())
        val barValue = DailyScheduleWithShows()

        bar.value = barValue
        verify(observer).onChanged(barValue)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun testEmptySchedule() {
        dailyScheduleViewModel.schedule.observeForever(mock())
        dailyScheduleViewModel.isEmpty.observeForever(mock())

        val date = Date()

        //test loading from network with filled schedule
        `when`(dailyScheduleRepository.loadScheduleFromCache(date)).thenReturn(LiveDataTestUtil
                .createFilledDailyScheduleWithShowsLiveData())
        dailyScheduleViewModel.loadSchedule(date)
        assertThat(dailyScheduleViewModel.isEmpty.value, `is`(false))

        //test loading from network with empty schedule
        `when`(dailyScheduleRepository.loadScheduleFromCache(date)).thenReturn(LiveDataTestUtil.createEmptyDailyScheduleWithShowsLiveData())
        dailyScheduleViewModel.loadSchedule(date)
        assertThat(dailyScheduleViewModel.isEmpty.value, `is`(true))
    }
}