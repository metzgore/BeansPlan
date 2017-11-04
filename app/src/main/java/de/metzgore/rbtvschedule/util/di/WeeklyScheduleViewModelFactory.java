package de.metzgore.rbtvschedule.util.di;

import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.metzgore.rbtvschedule.shared.ScheduleRepository;
import de.metzgore.rbtvschedule.weeklyschedule.WeeklyScheduleViewModel;

@Singleton
public class WeeklyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private final ScheduleRepository repo;

    @Inject
    public WeeklyScheduleViewModelFactory(ScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    public WeeklyScheduleViewModel create(Class modelClass) {
        return new WeeklyScheduleViewModel(repo);
    }
}
