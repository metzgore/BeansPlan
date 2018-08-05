package de.metzgore.beansplan.util

import android.content.Context
import android.graphics.drawable.LayerDrawable
import de.metzgore.beansplan.R

object BadgeDrawableUtil {

    fun setNumber(context: Context, icon: LayerDrawable, number: Int) {

        val badge: BadgeDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_badge)
        badge = if (reuse != null && reuse is BadgeDrawable) {
            reuse
        } else {
            BadgeDrawable(context)
        }

        badge.setNumber(number)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
    }
}