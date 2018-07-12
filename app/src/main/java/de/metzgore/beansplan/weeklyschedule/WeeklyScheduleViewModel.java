package de.metzgore.beansplan.weeklyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.Status;
import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules;

public class WeeklyScheduleViewModel extends ViewModel {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final LiveData<Resource<WeeklyScheduleWithDailySchedules>> schedule;
    public final LiveData<Boolean> isEmpty;
    public final LiveData<Boolean> isLoading;

    public WeeklyScheduleViewModel(WeeklyScheduleRepository scheduleRepo) {
        schedule = Transformations.switchMap(refresh, scheduleRepo::loadSchedule);
        isEmpty = Transformations.map(schedule, schedule -> schedule == null || schedule.getData
                () == null || schedule.getData().getDailySchedulesWithShows().isEmpty());
        isLoading = Transformations.map(schedule, schedule -> schedule.getStatus() == Status
                .LOADING);
    }

    public LiveData<Resource<WeeklyScheduleWithDailySchedules>> getSchedule() {
        return schedule;
    }

    public void loadScheduleFromNetwork() {
        refresh.setValue(true);
    }

    public void loadSchedule() {
        refresh.setValue(isEmpty.getValue() == null || isEmpty.getValue());
    }
}
