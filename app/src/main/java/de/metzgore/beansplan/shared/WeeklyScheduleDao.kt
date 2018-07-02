package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.WeeklyScheduleResponse

interface WeeklyScheduleDao {

    fun save(item: WeeklyScheduleResponse?)

    fun get(): WeeklyScheduleResponse?

    companion object {

        const val WEEKLY_SCHEDULE_KEY = "weekly_schedule_key"
    }
}
