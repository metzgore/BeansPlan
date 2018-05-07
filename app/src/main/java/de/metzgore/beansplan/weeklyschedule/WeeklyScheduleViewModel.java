package de.metzgore.beansplan.weeklyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.Status;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.shared.IScheduleViewModel;
import de.metzgore.beansplan.shared.ScheduleRepository;

public class WeeklyScheduleViewModel extends ViewModel implements IScheduleViewModel<WeeklySchedule> {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final LiveData<Resource<WeeklySchedule>> schedule;
    public final LiveData<Boolean> isEmpty;
    public final LiveData<Boolean> isLoading;

    @Inject
    public WeeklyScheduleViewModel(ScheduleRepository scheduleRepo) {
        schedule = Transformations.switchMap(refresh, scheduleRepo::loadWeeklySchedule);
        isEmpty = Transformations.map(schedule, schedule -> schedule == null || schedule.data == null || schedule.data.isEmpty());
        isLoading = Transformations.map(schedule, schedule -> schedule.status == Status.LOADING);
    }

    @Override
    public LiveData<Resource<WeeklySchedule>> getSchedule() {
        return schedule;
    }

    @Override
    public void loadScheduleFromNetwork() {
        refresh.setValue(true);
    }

    @Override
    public void loadSchedule() {
        refresh.setValue(isEmpty.getValue() == null || isEmpty.getValue());
    }
}
