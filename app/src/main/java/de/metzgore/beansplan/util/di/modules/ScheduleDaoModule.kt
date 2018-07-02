package de.metzgore.beansplan.util.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.dailyschedule.DailyScheduleDaoImpl
import de.metzgore.beansplan.data.room.BeansPlanDb
import de.metzgore.beansplan.shared.DailyScheduleDao
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleDaoImpl
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRoomDao
import javax.inject.Singleton

@Module(includes = [(ContextModule::class)])
class ScheduleDaoModule(private val cacheOnly: Boolean) {

    @Provides
    @Singleton
    fun providesDailyScheduleDao(context: Context): DailyScheduleDao {
        return DailyScheduleDaoImpl(context, cacheOnly)
    }

    @Provides
    @Singleton
    fun providesWeeklyScheduleDao(context: Context): WeeklyScheduleDao {
        return WeeklyScheduleDaoImpl(context, cacheOnly)
    }

    @Singleton
    @Provides
    fun provideDb(context: Context): BeansPlanDb {
        return Room
                .databaseBuilder(context, BeansPlanDb::class.java, "beansplan.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun providesWeeklyScheduleRoomDao(db: BeansPlanDb): WeeklyScheduleRoomDao {
        return db.scheduleDao()
    }
}
