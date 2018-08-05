package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val timestamp: Date
)