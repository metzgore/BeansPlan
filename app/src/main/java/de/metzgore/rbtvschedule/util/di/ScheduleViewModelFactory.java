package de.metzgore.rbtvschedule.util.di;

import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleViewModel;
import de.metzgore.rbtvschedule.shared.ScheduleRepository;

@Singleton
public class ScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository repo;

    @Inject
    public ScheduleViewModelFactory(ScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    public DailyScheduleViewModel create(Class modelClass) {
        return new DailyScheduleViewModel(repo);
    }
}
