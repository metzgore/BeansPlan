package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class DailyScheduleWithShows {
    @Embedded
    lateinit var dailySchedule: DailySchedule

    @Relation(parentColumn = "id", entityColumn = "scheduleId")
    lateinit var shows: List<Show>

    fun sortedShows() = shows.sortedBy { show -> show.timeStart }
}