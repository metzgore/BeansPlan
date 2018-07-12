package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.Date;

import de.metzgore.beansplan.data.room.DailyScheduleWithShows;

public class DailyScheduleViewModel extends ViewModel {

    private final MutableLiveData<Date> dateToLoad = new MutableLiveData<>();
    private final LiveData<DailyScheduleWithShows> schedule;
    public final LiveData<Boolean> isEmpty;

    public DailyScheduleViewModel(DailyScheduleRepository scheduleRepo) {
        schedule = Transformations.switchMap(dateToLoad, scheduleRepo::loadScheduleFromCache);
        isEmpty = Transformations.map(schedule, schedule -> schedule == null || schedule.shows
                .isEmpty());
    }

    public LiveData<DailyScheduleWithShows> getSchedule() {
        return schedule;
    }

    public void loadSchedule(@NonNull Date date) {
        dateToLoad.setValue(date);
    }
}
