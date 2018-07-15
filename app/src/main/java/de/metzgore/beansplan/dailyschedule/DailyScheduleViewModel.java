package de.metzgore.beansplan.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.Date;

import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows;
import de.metzgore.beansplan.data.room.Reminder;
import de.metzgore.beansplan.data.room.Show;

public class DailyScheduleViewModel extends ViewModel {

    private final MutableLiveData<Date> dateToLoad = new MutableLiveData<>();
    private final LiveData<DailyScheduleWithShows> schedule;
    private final DailyScheduleRepository repo;
    public final LiveData<Boolean> isEmpty;

    public DailyScheduleViewModel(DailyScheduleRepository scheduleRepo) {
        repo = scheduleRepo;
        schedule = Transformations.switchMap(dateToLoad, repo::loadScheduleFromCache);
        isEmpty = Transformations.map(schedule, schedule -> schedule == null || schedule.shows
                .isEmpty());
    }

    public LiveData<DailyScheduleWithShows> getSchedule() {
        return schedule;
    }

    public void loadSchedule(@NonNull Date date) {
        dateToLoad.setValue(date);
    }

    public void upsertReminder(Show show, Reminder reminder) {
        repo.upsertReminder(show, reminder);
    }

    public void deleteReminder(Show show, Reminder reminder) {
        repo.deleteReminder(show, reminder);
    }
}
