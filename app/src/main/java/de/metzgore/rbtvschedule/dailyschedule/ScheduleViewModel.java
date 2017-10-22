package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import de.metzgore.rbtvschedule.data.Schedule;

public class ScheduleViewModel extends ViewModel {

    private LiveData<Schedule> schedule;
    private ScheduleRepository scheduleRepo;

    public ScheduleViewModel() {
        scheduleRepo = new ScheduleRepository();

        if (schedule != null) {
            return;
        }
        schedule = scheduleRepo.getScheduleOfToday();
    }

    public LiveData<Schedule> getSchedule() {
        return schedule;
    }
}
