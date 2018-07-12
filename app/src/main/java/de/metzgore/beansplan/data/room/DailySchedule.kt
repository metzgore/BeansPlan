package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
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