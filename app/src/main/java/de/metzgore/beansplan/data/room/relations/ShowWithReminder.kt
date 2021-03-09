package de.metzgore.beansplan.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show

data class ShowWithReminder(
    @Embedded
    val show: Show,

    @Relation(parentColumn = "reminderId", entityColumn = "id")
    val reminder: Reminder?
)