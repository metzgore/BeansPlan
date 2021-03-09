package de.metzgore.beansplan.dailyschedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

class DailyScheduleRepositoryTest {

    private val scheduleDao = mock<ScheduleRoomDao>()
    private val repo = DailyScheduleRepository(scheduleDao, InstantAppExecutors())

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