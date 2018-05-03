package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.rbtvschedule.data.DailySchedule;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.shared.IScheduleViewModel;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;

public class DailyScheduleViewModel extends ViewModel implements IScheduleViewModel<DailySchedule> {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final MutableLiveData<Resource<DailySchedule>> schedule = new MutableLiveData<>();
    private final MediatorLiveData<Resource<DailySchedule>> scheduleMerger = new MediatorLiveData<>();
    public LiveData<Boolean> isEmpty = Transformations.map(scheduleMerger, schedule -> schedule == null || schedule.data == null || schedule.data.getShows().isEmpty());

    @Inject
    public DailyScheduleViewModel(ScheduleRepository scheduleRepo) {
        LiveData<Resource<DailySchedule>> scheduleFromRepo = Transformations.switchMap(refresh, scheduleRepo::loadScheduleOfToday);
        scheduleMerger.addSource(scheduleFromRepo, scheduleMerger::setValue);
    }

    public DailyScheduleViewModel(Resource<DailySchedule> scheduleResource) {
        scheduleMerger.addSource(schedule, scheduleMerger::setValue);
        this.schedule.setValue(scheduleResource);
    }

    public LiveData<Resource<DailySchedule>> getSchedule() {
        return scheduleMerger;
    }

    @Override
    public void loadSchedule(boolean forceFromNetwork) {
        refresh.setValue(forceFromNetwork);
    }

    public void setSchedule(Resource<DailySchedule> scheduleResource) {
        schedule.setValue(scheduleResource);
    }
}
