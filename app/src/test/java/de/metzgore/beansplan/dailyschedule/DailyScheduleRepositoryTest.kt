package de.metzgore.beansplan.dailyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

class DailyScheduleRepositoryTest {

    private val scheduleDao = mock(ScheduleRoomDao::class.java)
    private val repo = DailyScheduleRepository(scheduleDao)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadSchedule() {
        val date = Date()

        `when`(scheduleDao.getDailyScheduleWithShows(date)).thenReturn(LiveDataTestUtil
                .createFilledDailyScheduleWithShowsLiveData())

        repo.loadScheduleFromCache(date)
        verify(scheduleDao).getDailyScheduleWithShows(date)
        //TODO check this
        //verify(scheduleDao).getDailyScheduleWithShowsDistinct(date)
    }
}