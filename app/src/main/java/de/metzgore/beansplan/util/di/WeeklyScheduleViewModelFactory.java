package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleViewModel;

@Singleton
public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository repo;

    //TODO unite with ScheduleViewModelFactory
    @Inject
    public WeeklyScheduleViewModelFactory(ScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public WeeklyScheduleViewModel create(@NonNull Class modelClass) {
        return new WeeklyScheduleViewModel(repo);
    }
}
