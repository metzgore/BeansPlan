package de.metzgore.beansplan.weeklyschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.data.room.relations.WeeklyScheduleWithDailySchedules

class WeeklyScheduleViewModel(private val scheduleRepo: WeeklyScheduleRepository) : ViewModel() {

    private val refresh = MutableLiveData<Boolean>()
    val schedule: LiveData<Resource<WeeklyScheduleWithDailySchedules>> = Transformations.switchMap(refresh) {
        scheduleRepo.loadSchedule(it)
    }
    val isEmpty: LiveData<Boolean> = Transformations.map(schedule) { schedule ->
        schedule?.data == null || schedule.data.dailySchedulesWithShows.isEmpty()
    }
    val isLoading: LiveData<Boolean> = Transformations.map(schedule) { (status) ->
        status == Status.LOADING
    }

    fun loadScheduleFromNetwork() {
        refresh.value = true
    }

    fun loadSchedule() {
        refresh.value = isEmpty.value == null || isEmpty.value!!
    }
}
