package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.beansplan.dailyschedule.DailyScheduleViewModel;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.shared.ScheduleRepository;

@Singleton
public class ScheduleViewModelFactory implements ViewModelProvider.Factory {

    private ScheduleRepository repo;
    private DailySchedule dailySchedule;

    @Inject
    public ScheduleViewModelFactory(ScheduleRepository repo) {
        this.repo = repo;
    }

    public ScheduleViewModelFactory(DailySchedule dailySchedule) {
        this.dailySchedule = dailySchedule;
    }

    @Override
    @NonNull
    public DailyScheduleViewModel create(@NonNull Class modelClass) {
        if (dailySchedule != null)
            return new DailyScheduleViewModel(Resource.success(dailySchedule, false));
        else
            return new DailyScheduleViewModel(repo);
    }
}
