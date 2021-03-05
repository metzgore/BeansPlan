package de.metzgore.beansplan.util.di;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository;
import de.metzgore.beansplan.dailyschedule.DailyScheduleViewModel;

public class DailyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private DailyScheduleRepository repo;

    public DailyScheduleViewModelFactory(DailyScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public DailyScheduleViewModel create(@NonNull Class modelClass) {
        return new DailyScheduleViewModel(repo);
    }
}
