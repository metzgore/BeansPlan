package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.shared.ScheduleRepository
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRepository

import javax.inject.Singleton

@Module(includes = [(RbtvApiModule::class), (ScheduleDaoModule::class)])
class RepositoryModule {

    @Provides
    @Singleton
    fun providesDailyScheduleRepository(repo: DailyScheduleRepository): ScheduleRepository<DailySchedule> {
        return repo
    }

    @Provides
    @Singleton
    fun providesWeeklyScheduleRepository(repo: WeeklyScheduleRepository): ScheduleRepository<WeeklySchedule> {
        return repo
    }
}
