package de.metzgore.beansplan.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val timestamp: Date
)