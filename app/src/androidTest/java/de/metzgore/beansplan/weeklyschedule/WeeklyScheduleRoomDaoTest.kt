package de.metzgore.beansplan.weeklyschedule

import de.metzgore.beansplan.DbTest
import de.metzgore.beansplan.LiveDataTestUtil.getValue
import de.metzgore.beansplan.data.room.WeeklySchedule
import de.metzgore.beansplan.util.ClockImpl
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class WeeklyScheduleRoomDaoTest : DbTest() {

    @Test
    fun insertAndRead() {
        val timestamp = ClockImpl().nowInMillis()

        db.scheduleDao().insert(WeeklySchedule(timestamp = timestamp))
        val loaded = getValue(db.scheduleDao().getWeeklySchedule("weekly_schedule"))
        assertThat(loaded, notNullValue())
        assertThat(loaded.timestamp, `is`(timestamp))
    }
}