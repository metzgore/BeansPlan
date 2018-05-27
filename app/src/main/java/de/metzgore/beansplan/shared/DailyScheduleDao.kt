package de.metzgore.beansplan.shared

import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.testing.OpenForTesting

interface DailyScheduleDao {

    fun save(item: DailySchedule?)

    fun get(): DailySchedule?

    companion object {

        const val DAILY_SCHEDULE_KEY = "daily_schedule_key"
    }
}
