package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.AppExecutors
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.dailyschedule.DailyScheduleRepository
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRepository
import javax.inject.Singleton

@Module(includes = [(RbtvApiModule::class), (ScheduleDaoModule::class), (ClockModule::class)])
class RepositoryModule {

    @Provides
    @Singleton
    fun providesDailyScheduleRepository(dao: ScheduleRoomDao, appExecutors: AppExecutors): DailyScheduleRepository {
        return DailyScheduleRepository(dao, appExecutors)
    }

    @Provides
    @Singleton
    fun providesWeeklyScheduleRepository(api: RbtvScheduleApi, executors: AppExecutors, dao:
    ScheduleRoomDao, clock: Clock): WeeklyScheduleRepository {
        return WeeklyScheduleRepository(api, dao, executors, clock)
    }
}
