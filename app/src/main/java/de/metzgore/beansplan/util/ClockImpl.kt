package de.metzgore.beansplan.util

import org.threeten.bp.Instant

class ClockImpl : Clock {
    override fun nowInMillis() = Instant.now().toEpochMilli()
}
