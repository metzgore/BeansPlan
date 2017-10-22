package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.metzgore.rbtvschedule.data.Schedule;

public class ScheduleViewModel extends ViewModel {

    private LiveData<Schedule> schedule;

    @Inject
    public ScheduleViewModel(ScheduleRepository scheduleRepo) {
        if (schedule != null) {
            return;
        }
        schedule = scheduleRepo.getScheduleOfToday();
    }

    public LiveData<Schedule> getSchedule() {
        return schedule;
    }
}
