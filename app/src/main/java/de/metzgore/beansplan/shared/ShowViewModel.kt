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

class ShowViewModel(private val showWithReminder: ShowWithReminder, private val listener:
OnReminderButtonClickListener?) {

    val alpha: Float = if (showWithReminder.show.isOver) 0.3f else 1.0f

    val title: String = showWithReminder.show.title

    val topic: String = showWithReminder.show.topic

    val isOnYoutube: Boolean = !showWithReminder.show.youtubeId.isBlank()

    val isReminderIconVisible = !showWithReminder.show.isOver && !showWithReminder.show.isRunning

    val typeFormatted: String = when (showWithReminder.show.type) {
        ShowResponse.Type.LIVE -> "LIVE"
        ShowResponse.Type.PREMIERE -> "NEU"
        ShowResponse.Type.NONE -> ""
    }

    fun getTypeFormatted(context: Context): String {
        return when (showWithReminder.show.type) {
            ShowResponse.Type.LIVE -> context.getString(R.string.show_type_live)
            ShowResponse.Type.PREMIERE -> context.getString(R.string.show_type_premiere)
            ShowResponse.Type.NONE -> ""
        }
    }

    fun getReminderIcon(context: Context): Drawable {
        return if (showWithReminder.reminder?.get(0) == null) {
            ContextCompat.getDrawable(context, R.drawable.ic_reminder_add)!!
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_reminder_available)!!
        }
    }

    fun getBackground(context: Context): Drawable? {
        return if (showWithReminder.show.isRunning)
            ContextCompat.getDrawable(context, R.drawable.border_current_show)
        else
            null
    }

    fun getTypeBackground(context: Context): Drawable? {
        return when (showWithReminder.show.type) {
            ShowResponse.Type.LIVE -> ContextCompat.getDrawable(context, R.drawable.background_show_type_live)
            ShowResponse.Type.PREMIERE -> ContextCompat.getDrawable(context, R.drawable.background_show_type_premiere)
            else -> null
        }
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
