package de.metzgore.beansplan.util.di.modules

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.data.room.BeansPlanDb
import de.metzgore.beansplan.data.room.ScheduleRoomDao
import javax.inject.Singleton

@Module(includes = [(ContextModule::class)])
class ScheduleDaoModule(private val inMemory: Boolean) {

    @Singleton
    @Provides
    fun provideDb(context: Context) = if (inMemory) {
        Room.inMemoryDatabaseBuilder(context, BeansPlanDb::class.java).build()
    } else {
        Room.databaseBuilder(context, BeansPlanDb::class.java, "beansplan.db").fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideScheduleRoomDao(db: BeansPlanDb): ScheduleRoomDao {
        return db.scheduleDao()
    }
}
