package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleRoomDao

@Database(
        entities = [
            DailySchedule::class,
            Show::class,
            WeeklySchedule::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class BeansPlanDb : RoomDatabase() {

    abstract fun scheduleDao(): WeeklyScheduleRoomDao
}