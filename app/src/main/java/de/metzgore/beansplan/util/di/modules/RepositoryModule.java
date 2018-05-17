package de.metzgore.beansplan.util.di.modules;

import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.shared.ScheduleRepository;

import javax.inject.Singleton;

@Module(includes = {RbtvApiModule.class, DailyScheduleDaoModule.class})
public class RepositoryModule {

    @Provides
    @Singleton
    public ScheduleRepository<DailySchedule> providesDailyScheduleRepository(DailyScheduleRepository repo) {
        return repo;
    }
}
