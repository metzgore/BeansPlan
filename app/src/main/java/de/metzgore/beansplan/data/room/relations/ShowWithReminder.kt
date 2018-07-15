package de.metzgore.beansplan.data.room.relations

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show

class ShowWithReminder {
    @Embedded
    lateinit var show: Show

    //TODO better one-to-one relation
    @Relation(parentColumn = "reminderId", entityColumn = "id")
    var reminder: List<Reminder>? = null
}