package de.metzgore.beansplan.data.room.relations

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import de.metzgore.beansplan.data.room.DailySchedule
import de.metzgore.beansplan.data.room.WeeklySchedule
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