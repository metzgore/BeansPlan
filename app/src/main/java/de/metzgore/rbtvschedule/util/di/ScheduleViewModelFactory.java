package de.metzgore.rbtvschedule.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleViewModel;
import de.metzgore.rbtvschedule.data.DailySchedule;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;

@Singleton
public class ScheduleViewModelFactory implements ViewModelProvider.Factory {

    private ScheduleRepository repo;
    private boolean forceRefresh;
    private DailySchedule dailySchedule;

    @Inject
    public ScheduleViewModelFactory(ScheduleRepository repo, boolean forceRefresh) {
        this.repo = repo;
        this.forceRefresh = forceRefresh;
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
            return new DailyScheduleViewModel(repo, forceRefresh);
    }
}
