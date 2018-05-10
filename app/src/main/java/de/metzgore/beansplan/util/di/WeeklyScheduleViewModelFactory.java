package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleViewModel;

public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository<WeeklySchedule> repo;

    //TODO unite with DailyScheduleViewModelFactory
    public WeeklyScheduleViewModelFactory(ScheduleRepository<WeeklySchedule> repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public WeeklyScheduleViewModel create(@NonNull Class modelClass) {
        return new WeeklyScheduleViewModel(repo);
    }
}
