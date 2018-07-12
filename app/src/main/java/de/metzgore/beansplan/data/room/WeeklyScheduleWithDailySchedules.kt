package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import java.util.*

class WeeklyScheduleWithDailySchedules {
    @Embedded
    lateinit var weeklySchedule: WeeklySchedule

    @Relation(parentColumn = "id", entityColumn = "scheduleId", projection = ["id"], entity =
    DailySchedule::class)
    lateinit var dailySchedulesWithShows: List<Date>

    //TODO nullpointer
    @Ignore
    fun getStartDate() = dailySchedulesWithShows.firstOrNull()

    @Ignore
    fun getEndDate() = dailySchedulesWithShows.lastOrNull()
}