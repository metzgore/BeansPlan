package de.metzgore.beansplan.data.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import de.metzgore.beansplan.data.ShowResponse
import java.util.*

@Entity
(foreignKeys = [(ForeignKey(entity = DailySchedule::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("scheduleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE))])
class Show(
        @PrimaryKey
        val id: Long,
        val scheduleId: Date,
        val title: String,
        val topic: String,
        val timeStart: Date,
        val timeEnd: Date,
        val length: Int,
        val game: String,
        val youtubeId: String,
        val type: ShowResponse.Type
) {
    @Ignore
    private val comparisonDate: Date = Date()

    val isRunning: Boolean
        get() {
            return !comparisonDate.before(timeStart) && !comparisonDate.after(timeEnd)
        }

    val isOver get() = timeEnd.before(comparisonDate)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Show) return false

        if (id != other.id) return false
        if (scheduleId != other.scheduleId) return false
        if (title != other.title) return false
        if (topic != other.topic) return false
        if (timeStart != other.timeStart) return false
        if (timeEnd != other.timeEnd) return false
        if (length != other.length) return false
        if (game != other.game) return false
        if (youtubeId != other.youtubeId) return false
        if (type != other.type) return false
        if (isRunning != other.isRunning) return false
        if (isOver != other.isOver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + scheduleId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + timeStart.hashCode()
        result = 31 * result + timeEnd.hashCode()
        result = 31 * result + length
        result = 31 * result + game.hashCode()
        result = 31 * result + youtubeId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + isRunning.hashCode()
        result = 31 * result + isOver.hashCode()
        return result
    }
}