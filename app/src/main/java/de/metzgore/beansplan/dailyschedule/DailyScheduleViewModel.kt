package de.metzgore.beansplan.dailyschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import java.util.*

class DailyScheduleViewModel(private val repo: DailyScheduleRepository) : ViewModel() {

    private val dateToLoad = MutableLiveData<Date>()
    val schedule: LiveData<DailyScheduleWithShows> = Transformations.switchMap(dateToLoad) { repo.loadScheduleFromCache(it) }
    val isEmpty: LiveData<Boolean> = Transformations.map(schedule) { schedule ->
        schedule == null || schedule.shows.isEmpty()
    }

    fun loadSchedule(date: Date) {
        dateToLoad.value = date
    }

    fun upsertReminder(showWithReminder: ShowWithReminder) {
        repo.upsertReminder(showWithReminder)
    }

    fun deleteReminder(showWithReminder: ShowWithReminder) {
        repo.deleteReminder(showWithReminder)
    }
}
