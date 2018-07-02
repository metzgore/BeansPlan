package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository
import de.metzgore.beansplan.data.room.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRepository
import javax.inject.Singleton

@Module(includes = [(RbtvApiModule::class), (ScheduleDaoModule::class)])
class RepositoryModule {

    @Provides
    @Singleton
    fun providesDailyScheduleRepository(repo: DailyScheduleRepository):
            ScheduleRepository<DailyScheduleWithShows> {
        return repo
    }

    @Provides
    @Singleton
    fun providesWeeklyScheduleRepository(repo: WeeklyScheduleRepository):
            ScheduleRepository<WeeklyScheduleWithDailySchedules> {
        return repo
    }
}
