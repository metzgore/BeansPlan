package de.metzgore.beansplan.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        entities = [
            DailySchedule::class,
            Show::class,
            WeeklySchedule::class,
            Reminder::class],
        version = 1
)
@TypeConverters(RoomConverters::class)
abstract class BeansPlanDb : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleRoomDao
}