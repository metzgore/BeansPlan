package de.metzgore.beansplan.data.room.relations

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import de.metzgore.beansplan.data.room.DailySchedule
import de.metzgore.beansplan.data.room.Show

class DailyScheduleWithShows {
    @Embedded
    lateinit var dailySchedule: DailySchedule

    @Relation(parentColumn = "id", entityColumn = "scheduleId", entity = Show::class)
    lateinit var shows: List<ShowWithReminder>

    fun sortedShows() = shows.filter { show -> !show.show.deleted }.sortedBy { show -> show.show.timeStart }
}