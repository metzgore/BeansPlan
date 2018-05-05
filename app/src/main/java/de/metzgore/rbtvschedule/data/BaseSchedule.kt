package de.metzgore.rbtvschedule.data

abstract class BaseSchedule {

    var timestamp: Long = 0
        private set

    fun updateTimestamp() {
        timestamp = System.currentTimeMillis()
    }
}
