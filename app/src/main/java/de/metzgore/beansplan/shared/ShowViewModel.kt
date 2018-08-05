package de.metzgore.beansplan.shared

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.ShowResponse
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import org.apache.commons.lang3.time.DurationFormatUtils

class ShowViewModel(private val showWithReminder: ShowWithReminder, private val listener:
OnReminderButtonClickListener?) {

    val alpha: Float = if (showWithReminder.show.isOver) 0.3f else 1.0f

    val title: String = showWithReminder.show.title

    val topic: String = showWithReminder.show.topic

    val isOnYoutube: Boolean = !showWithReminder.show.youtubeId.isBlank()

    val reminderVisible = !showWithReminder.show.isOver && !showWithReminder.show.isRunning

    val typeFormatted: String = when (showWithReminder.show.type) {
        ShowResponse.Type.LIVE, ShowResponse.Type.PREMIERE -> showWithReminder.show.type.toString()
        ShowResponse.Type.NONE -> ""
    }

    val lengthFormatted: String = DurationFormatUtils.formatDuration((showWithReminder.show.length * 1000)
            .toLong(),
            if (showWithReminder.show.length > 3600) "H 'h' mm 'min'" else "m 'min'")

    fun getBackground(context: Context): Drawable? {
        return if (showWithReminder.show.isRunning) ContextCompat.getDrawable(context, R.drawable.border_current_show) else
            null
    }

    fun getStartTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, showWithReminder.show.timeStart.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun getEndTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, showWithReminder.show.timeEnd.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun openVideo(context: Context) {
        if (isOnYoutube) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube" +
                    ".com/watch?v=${showWithReminder.show.youtubeId}")))
        }
    }

    fun saveReminder() {
        if (showWithReminder.reminder?.get(0) == null) {
            val newShowWithReminder = ShowWithReminder()
            newShowWithReminder.show = showWithReminder.show
            newShowWithReminder.reminder = listOf(Reminder(timestamp = showWithReminder.show.timeStart))
            listener?.onUpsertReminder(newShowWithReminder)
        } else {
            listener?.deleteOrUpdateReminder(showWithReminder)
        }
    }
}
