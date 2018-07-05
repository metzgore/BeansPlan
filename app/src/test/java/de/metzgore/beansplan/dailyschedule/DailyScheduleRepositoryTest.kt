package de.metzgore.beansplan.dailyschedule

import android.arch.core.executor.testing.InstantTaskExecutorRule
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


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
        repo.loadScheduleFromCache(false)
        verify(scheduleDao).get()
    }
}