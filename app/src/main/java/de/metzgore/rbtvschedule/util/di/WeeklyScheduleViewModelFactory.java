package de.metzgore.rbtvschedule.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.rbtvschedule.shared.ScheduleRepository;
import de.metzgore.rbtvschedule.weeklyschedule.WeeklyScheduleViewModel;

@Singleton
public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository repo;
    private final boolean forceRefresh;

    //TODO unite with ScheduleViewModelFactory
    @Inject
    public WeeklyScheduleViewModelFactory(ScheduleRepository repo, boolean forceRefresh) {
        this.repo = repo;
        this.forceRefresh = forceRefresh;
    }

    @Override
    @NonNull
    public WeeklyScheduleViewModel create(@NonNull Class modelClass) {
        return new WeeklyScheduleViewModel(repo, forceRefresh);
    }
}
