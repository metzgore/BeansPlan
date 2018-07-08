package de.metzgore.beansplan.weeklyschedule

import TestUtils
import de.metzgore.beansplan.DbTest
import de.metzgore.beansplan.LiveDataTestUtil.getValue
import de.metzgore.beansplan.data.room.WeeklySchedule
import de.metzgore.beansplan.util.ClockImpl
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ScheduleRoomDaoTest : DbTest() {

    @Test
    fun insertAndRead() {
        val timestamp = ClockImpl().nowInMillis()
        val rawSchedule = TestUtils.createWeeklySchedule()

        db.scheduleDao().upsert(WeeklySchedule(timestamp = timestamp, weeklyScheduleRaw = rawSchedule))
        val loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())
        assertThat(loaded, notNullValue())
        assertThat(loaded.weeklySchedule.timestamp, `is`(timestamp))
    }
}