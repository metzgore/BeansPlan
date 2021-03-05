package de.metzgore.beansplan.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity
(foreignKeys = [(ForeignKey(entity = WeeklySchedule::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("scheduleId"),
        onDelete = ForeignKey.CASCADE))],
        indices = [(Index(value = ["scheduleId"]))])
data class DailySchedule(
        @PrimaryKey
        val id: Date,
        val scheduleId: String
)