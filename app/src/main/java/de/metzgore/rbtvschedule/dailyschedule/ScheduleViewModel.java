package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.rbtvschedule.data.Schedule;

public class ScheduleViewModel extends ViewModel {

    private LiveData<Schedule> schedule;
    private ScheduleRepository scheduleRepo;

    @Inject
    public ScheduleViewModel(ScheduleRepository scheduleRepo) {
        if (schedule != null) {
            return;
        }
        this.scheduleRepo = scheduleRepo;

    }

    public LiveData<Schedule> getSchedule() {
        return schedule;
    }

    public void loadSchedule(boolean forceRefresh) {
        schedule = scheduleRepo.loadScheduleOfToday();
    }
}
