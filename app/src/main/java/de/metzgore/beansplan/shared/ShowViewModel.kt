package de.metzgore.beansplan.shared

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import de.metzgore.beansplan.R
import de.metzgore.beansplan.dailyschedule.DailyScheduleAdapter
import de.metzgore.beansplan.data.ShowResponse
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import org.apache.commons.lang3.time.DurationFormatUtils
import java.util.*

class ShowViewModel(private val show: Show, private val reminder: Reminder?, private val listener:
DailyScheduleAdapter
.OnDeleteButtonClickListener?) {

    val alpha: Float = if (show.isOver) 0.3f else 1.0f

    val title: String = show.title

    val topic: String = show.topic

    val isOnYoutube: Boolean = !show.youtubeId.isBlank()

    val reminderVisible = !show.isOver && !show.isRunning

    val typeFormatted: String = when (show.type) {
        ShowResponse.Type.LIVE, ShowResponse.Type.PREMIERE -> show.type.toString()
        ShowResponse.Type.NONE -> ""
    }

    val lengthFormatted: String = DurationFormatUtils.formatDuration((show.length * 1000)
            .toLong(),
            if (show.length > 3600) "H 'h' mm 'min'" else "m 'min'")

    fun getBackground(context: Context): Drawable? {
        return if (show.isRunning) ContextCompat.getDrawable(context, R.drawable.border_current_show) else
            null
    }

    fun getStartTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, show.timeStart.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun getEndTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, show.timeEnd.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun openVideo(context: Context) {
        if (isOnYoutube) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube" +
                    ".com/watch?v=${show.youtubeId}")))
        }
    }

    fun saveReminder() {
        if (reminder == null) {
            listener?.onUpsertReminder(show, Reminder(timestamp = Date()))
        } else {
            listener?.deleteReminder(show, reminder)
        }
    }
}
