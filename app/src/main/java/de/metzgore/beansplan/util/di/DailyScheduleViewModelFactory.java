package de.metzgore.beansplan.util.di;

import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import de.metzgore.beansplan.dailyschedule.DailyScheduleViewModel;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.shared.ScheduleRepository;

public class DailyScheduleViewModelFactory implements ViewModelProvider.Factory {

    private ScheduleRepository<DailySchedule> repo;
    private DailySchedule dailySchedule;

    public DailyScheduleViewModelFactory(ScheduleRepository<DailySchedule> repo) {
        this.repo = repo;
    }

    public DailyScheduleViewModelFactory(DailySchedule dailySchedule) {
        this.dailySchedule = dailySchedule;
    }

    @Override
    @NonNull
    public DailyScheduleViewModel create(@NonNull Class modelClass) {
        if (dailySchedule != null)
            return new DailyScheduleViewModel(Resource.Companion.success(dailySchedule, false));
        else
            return new DailyScheduleViewModel(repo);
    }
}
