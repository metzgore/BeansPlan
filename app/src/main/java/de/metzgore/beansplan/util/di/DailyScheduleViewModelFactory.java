package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import de.metzgore.beansplan.dailyschedule.DailyScheduleViewModel;
import de.metzgore.beansplan.data.room.DailyScheduleWithShows;
import de.metzgore.beansplan.shared.ScheduleRepository;

public class DailyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private ScheduleRepository<DailyScheduleWithShows> repo;

    public DailyScheduleViewModelFactory(ScheduleRepository<DailyScheduleWithShows> repo) {
        this.repo = repo;
    }

    @Override
    @NonNull
    public DailyScheduleViewModel create(@NonNull Class modelClass) {
        return new DailyScheduleViewModel(repo);
    }
}
