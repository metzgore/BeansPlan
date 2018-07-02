package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import de.metzgore.beansplan.data.WeeklyScheduleResponse

@Entity
data class WeeklySchedule(
        @PrimaryKey
        val id: String = "weekly_schedule",
        val timestamp: Long,
        val weeklyScheduleRaw: WeeklyScheduleResponse
)