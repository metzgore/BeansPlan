package de.metzgore.beansplan.reminders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import de.metzgore.beansplan.LiveDataTestUtil
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.util.archcomponents.Event
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class RemindersViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val remindersRepository = mock<RemindersRepository>()
    private val remindersViewModel = RemindersViewModel(remindersRepository)

    @Test
    fun testNull() {
        assertThat(remindersViewModel.reminders, nullValue())
        assertThat(remindersViewModel.isEmpty, notNullValue())
        assertThat(remindersViewModel.isEmpty.value, nullValue())
        assertThat(remindersViewModel.triggerDeletionDialog, notNullValue())
        assertThat(remindersViewModel.triggerDeletionOrUpdateDialog, notNullValue())
        assertThat(remindersViewModel.triggerTimePickerDialog, notNullValue())
        verify(remindersRepository, times(1)).loadReminders()
        verify(remindersRepository, never()).loadRemindersSync()
    }

    @Test
    fun testLoadReminders() {
        `when`(remindersRepository.loadReminders()).thenReturn(LiveDataTestUtil
                .createFilledShowWithReminderLiveData())

        val remindersViewModel = RemindersViewModel(remindersRepository)

        remindersViewModel.reminders.observeForever(mock())
        remindersViewModel.isEmpty.observeForever(mock())

        assertThat(remindersViewModel.reminders, notNullValue())
        assertThat(remindersViewModel.isEmpty.value, `is`(false))
    }

    @Test
    fun testLoadEmptyReminders() {
        `when`(remindersRepository.loadReminders()).thenReturn(LiveDataTestUtil
                .createEmptyShowWithReminderLiveData())

        val remindersViewModel = RemindersViewModel(remindersRepository)

        remindersViewModel.reminders.observeForever(mock())
        remindersViewModel.isEmpty.observeForever(mock())

        assertThat(remindersViewModel.reminders, notNullValue())
        assertThat(remindersViewModel.isEmpty.value, `is`(true))
    }

    @Test
    fun testTriggerDeletionDialog() {
        val showWithReminder = ShowWithReminder()

        val observer = mock<Observer<Event<ShowWithReminder>>>()
        remindersViewModel.triggerDeletionDialog.observeForever(observer)

        remindersViewModel.triggerDeletionDialog(showWithReminder)
        verify(observer, times(1)).onChanged(any())

        assertThat(getValue(remindersViewModel.triggerDeletionDialog).getContentIfNotHandled(), notNullValue())
        assertThat(getValue(remindersViewModel.triggerDeletionDialog).getContentIfNotHandled(), nullValue())
        assertThat(getValue(remindersViewModel.triggerDeletionDialog).peekContent(), `is`(showWithReminder))

        remindersViewModel.triggerDeletionDialog(showWithReminder)
        verify(observer, times(2)).onChanged(any())
        assertThat(getValue(remindersViewModel.triggerDeletionDialog).getContentIfNotHandled(), `is`(showWithReminder))
    }

    @Test
    fun testTriggerDeletionOrUpdateDialog() {
        val showWithReminder = ShowWithReminder()

        val observer = mock<Observer<Event<ShowWithReminder>>>()
        remindersViewModel.triggerDeletionOrUpdateDialog.observeForever(observer)

        remindersViewModel.triggerDeletionOrUpdateDialog(showWithReminder)
        verify(observer, times(1)).onChanged(any())

        assertThat(getValue(remindersViewModel.triggerDeletionOrUpdateDialog).getContentIfNotHandled(), notNullValue())
        assertThat(getValue(remindersViewModel.triggerDeletionOrUpdateDialog).getContentIfNotHandled(), nullValue())
        assertThat(getValue(remindersViewModel.triggerDeletionOrUpdateDialog).peekContent(), `is`(showWithReminder))

        remindersViewModel.triggerDeletionOrUpdateDialog(showWithReminder)
        verify(observer, times(2)).onChanged(any())
        assertThat(getValue(remindersViewModel.triggerDeletionOrUpdateDialog).getContentIfNotHandled(), `is`(showWithReminder))
    }

    @Test
    fun testTriggerTimePickerDialog() {
        val showWithReminder = ShowWithReminder()

        val observer = mock<Observer<Event<ShowWithReminder>>>()
        remindersViewModel.triggerTimePickerDialog.observeForever(observer)

        remindersViewModel.triggerTimePickerDialog(showWithReminder)
        verify(observer, times(1)).onChanged(any())

        assertThat(getValue(remindersViewModel.triggerTimePickerDialog).getContentIfNotHandled(), notNullValue())
        assertThat(getValue(remindersViewModel.triggerTimePickerDialog).getContentIfNotHandled(), nullValue())
        assertThat(getValue(remindersViewModel.triggerTimePickerDialog).peekContent(), `is`(showWithReminder))

        remindersViewModel.triggerTimePickerDialog(showWithReminder)
        verify(observer, times(2)).onChanged(any())
        assertThat(getValue(remindersViewModel.triggerTimePickerDialog).getContentIfNotHandled(), `is`(showWithReminder))
    }

    @Test
    fun testUpsertReminder() {
        //TODO some more tests
        val showWithReminder = ShowWithReminder()

        remindersViewModel.upsertReminder(showWithReminder)

        verify(remindersRepository, times(1)).upsertReminder(showWithReminder)
    }

    @Test
    fun testDeleteReminder() {
        //TODO some more tests
        val showWithReminder = ShowWithReminder()

        remindersViewModel.deleteReminder(showWithReminder)

        verify(remindersRepository, times(1)).deleteReminder(showWithReminder)
    }

    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}