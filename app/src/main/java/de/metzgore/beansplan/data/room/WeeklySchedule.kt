package de.metzgore.beansplan.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.metzgore.beansplan.data.WeeklyScheduleResponse

@Entity
data class WeeklySchedule(
        @PrimaryKey
        val id: String = "weekly_schedule",
        val timestamp: Long,
        val weeklyScheduleRaw: WeeklyScheduleResponse
)