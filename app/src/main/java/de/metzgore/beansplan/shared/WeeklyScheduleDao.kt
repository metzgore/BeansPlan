package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.WeeklySchedule

interface WeeklyScheduleDao {

    fun save(item: WeeklySchedule?)

    fun get(): WeeklySchedule?

    companion object {

        const val WEEKLY_SCHEDULE_KEY = "weekly_schedule_key"
    }
}
