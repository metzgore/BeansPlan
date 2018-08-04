package de.metzgore.beansplan.reminders

import android.arch.core.executor.testing.InstantTaskExecutorRule
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.utils.InstantAppExecutors
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class RemindersRepositoryTest {

    private val scheduleDao = mock<ScheduleRoomDao>()
    private val repo = RemindersRepository(scheduleDao, InstantAppExecutors())

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadReminders() {
        `when`(scheduleDao.getShowsWithReminders()).thenReturn(LiveDataTestUtil.createShowWithReminderLiveData())

        repo.loadReminders()
        verify(scheduleDao).getShowsWithReminders()
        //TODO check this
        //verify(scheduleDao).getShowsWithRemindersDistict()
    }

    @Test
    fun loadRemindersSync() {
        `when`(scheduleDao.getShowsWithRemindersSync()).thenReturn(listOf(ShowWithReminder()))

        repo.loadRemindersSync()
        verify(scheduleDao).getShowsWithRemindersSync()
    }

    @Test
    fun upsertShowWithReminder() {
        val showWithReminder = ShowWithReminder()

        repo.upsertReminder(showWithReminder)
        verify(scheduleDao).upsertReminder(showWithReminder)
    }

    @Test
    fun deleteShowWithReminderByObject() {
        val showWithReminder = ShowWithReminder()

        repo.deleteReminder(showWithReminder)
        verify(scheduleDao).deleteReminder(showWithReminder)
    }

    @Test
    fun deleteShowWithReminderById() {
        repo.deleteReminder(ArgumentMatchers.anyLong())
        verify(scheduleDao).deleteReminder(ArgumentMatchers.anyLong())
    }
}