package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules;
import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleViewModel;

public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository<WeeklyScheduleWithDailySchedules> repo;

    public WeeklyScheduleViewModelFactory(ScheduleRepository<WeeklyScheduleWithDailySchedules>
                                                  repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public WeeklyScheduleViewModel create(@NonNull Class modelClass) {
        return new WeeklyScheduleViewModel(repo);
    }
}
