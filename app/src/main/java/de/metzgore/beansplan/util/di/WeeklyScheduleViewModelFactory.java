package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRepository;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleViewModel;

public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final WeeklyScheduleRepository repo;

    public WeeklyScheduleViewModelFactory(WeeklyScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public WeeklyScheduleViewModel create(@NonNull Class modelClass) {
        return new WeeklyScheduleViewModel(repo);
    }
}
