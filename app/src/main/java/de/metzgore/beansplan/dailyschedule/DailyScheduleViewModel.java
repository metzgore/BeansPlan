package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.*;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.Status;
import de.metzgore.beansplan.shared.IScheduleViewModel;
import de.metzgore.beansplan.shared.ScheduleRepository;

public class DailyScheduleViewModel extends ViewModel implements IScheduleViewModel<DailySchedule> {

    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    private final MutableLiveData<Resource<DailySchedule>> schedule = new MutableLiveData<>();
    private final MediatorLiveData<Resource<DailySchedule>> scheduleMerger = new MediatorLiveData<>();
    public LiveData<Boolean> isEmpty = Transformations.map(scheduleMerger, schedule -> schedule == null || schedule
            .getData() == null || schedule.getData().isEmpty());
    public LiveData<Boolean> isLoading = Transformations.map(scheduleMerger, schedule -> schedule
            .getStatus() == Status.LOADING);

    public DailyScheduleViewModel(ScheduleRepository<DailySchedule> scheduleRepo) {
        LiveData<Resource<DailySchedule>> scheduleFromRepo = Transformations.switchMap(refresh,
                scheduleRepo::loadSchedule);
        scheduleMerger.addSource(scheduleFromRepo, scheduleMerger::setValue);
    }

    //TODO refactoring?
    public DailyScheduleViewModel(DailySchedule dailySchedule) {
        scheduleMerger.addSource(schedule, scheduleMerger::setValue);
        setSchedule(dailySchedule);
    }

    public LiveData<Resource<DailySchedule>> getSchedule() {
        return scheduleMerger;
    }

    @Override
    public void loadScheduleFromNetwork() {
        refresh.setValue(true);
    }

    @Override
    public void loadSchedule() {
        refresh.setValue(isEmpty.getValue() == null || isEmpty.getValue());
    }

    public void setSchedule(DailySchedule dailySchedule) {
        schedule.setValue(Resource.Companion.success(dailySchedule, false));
    }
}
