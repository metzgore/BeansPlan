package de.metzgore.beansplan.shared

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.text.format.DateUtils
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.Show
import org.apache.commons.lang3.time.DurationFormatUtils

class ShowViewModel(private val show: Show) {

    val alpha: Float = if (show.isOver) 0.3f else 1.0f

    val title: String = show.title

    val topic: String = show.topic

    val isOnYoutube: Boolean = !TextUtils.isEmpty(show.youtubeId)

    val typeFormatted: String = when (show.type) {
        Show.Type.LIVE, Show.Type.PREMIERE -> show.type.toString()
        Show.Type.NONE -> ""
        else -> ""
    }

    val lengthFormatted: String = DurationFormatUtils.formatDuration((show.length * 1000).toLong(),
            if (show.length > 3600) "H 'h' mm 'min'" else "m 'min'")

    fun getBackground(context: Context): Drawable? {
        return if (show.isRunning) ContextCompat.getDrawable(context, R.drawable.border_current_show) else null
    }

    fun getStartTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, show.timeStart.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun getEndTimeFormatted(context: Context): String {
        return DateUtils.formatDateTime(context, show.timeEnd.time, DateUtils.FORMAT_SHOW_TIME)
    }

    fun openVideo(context: Context) {
        if (isOnYoutube) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${show.youtubeId}")))
        }
    }
}
