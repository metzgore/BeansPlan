package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.shared.IScheduleViewModel;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;

public class ScheduleViewModel extends ViewModel implements IScheduleViewModel<Schedule> {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final LiveData<Resource<Schedule>> schedule;

    @Inject
    public ScheduleViewModel(ScheduleRepository scheduleRepo) {
        schedule = Transformations.switchMap(refresh, forceFromNetwork -> {
            //TODO lambda
            return scheduleRepo.loadScheduleOfToday(forceFromNetwork);
        });
    }

    @Override
    public LiveData<Resource<Schedule>> getSchedule() {
        return schedule;
    }

    @Override
    public void loadSchedule(boolean forceFromNetwork) {
        refresh.setValue(forceFromNetwork);
    }
}
