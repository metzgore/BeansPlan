package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

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