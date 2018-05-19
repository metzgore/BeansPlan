package de.metzgore.beansplan.util.di.modules;

import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.shared.ScheduleRepository;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRepository;

import javax.inject.Singleton;

@Module(includes = {RbtvApiModule.class, ScheduleDaoModule.class})
public class RepositoryModule {

    @Provides
    @Singleton
    public ScheduleRepository<DailySchedule> providesDailyScheduleRepository(DailyScheduleRepository repo) {
        return repo;
    }

    @Provides
    @Singleton
    public ScheduleRepository<WeeklySchedule> providesWeeklyScheduleRepository(WeeklyScheduleRepository repo) {
        return repo;
    }
}
