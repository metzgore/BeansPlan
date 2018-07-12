package de.metzgore.beansplan.weeklyschedule

import TestUtils
import de.metzgore.beansplan.DbTest
import de.metzgore.beansplan.LiveDataTestUtil.getValue
import de.metzgore.beansplan.data.room.DailySchedule
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.WeeklySchedule
import de.metzgore.beansplan.util.Clock
import io.mockk.every
import io.mockk.mockk
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class ScheduleRoomDaoTest : DbTest() {

    val clock = mockk<Clock>()

    @Test
    fun insertAndReadWeeklySchedule() {
        val timestamp = 1L

        val rawSchedule = TestUtils.createWeeklyScheduleOneWeek()

        db.scheduleDao().insert(WeeklySchedule(timestamp = timestamp, weeklyScheduleRaw = rawSchedule))
        var loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        assertThat(loaded, notNullValue())
        assertThat(loaded.weeklySchedule.timestamp, `is`(timestamp))

        val updatedTimestamp = 2L
        db.scheduleDao().insert(WeeklySchedule(timestamp = updatedTimestamp, weeklyScheduleRaw =
        rawSchedule))
        loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        assertThat(loaded, notNullValue())
        assertThat(loaded.weeklySchedule.timestamp, `is`(timestamp))
    }

    @Test
    fun upsertAndReadWeeklySchedule() {
        val timestamp = 1L

        val rawSchedule = TestUtils.createWeeklyScheduleOneWeek()

        db.scheduleDao().upsert(WeeklySchedule(timestamp = timestamp, weeklyScheduleRaw = rawSchedule))
        var loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        assertThat(loaded, notNullValue())
        assertThat(loaded.weeklySchedule.timestamp, `is`(timestamp))

        val updatedTimestamp = 2L
        db.scheduleDao().upsert(WeeklySchedule(timestamp = updatedTimestamp, weeklyScheduleRaw =
        rawSchedule))
        loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        assertThat(loaded, notNullValue())
        assertThat(loaded.weeklySchedule.timestamp, `is`(updatedTimestamp))
    }

    @Test
    fun insertAndCompareRawWeeklySchedule() {
        val timestamp = 1L

        val rawSchedule = TestUtils.createWeeklyScheduleOneWeek()

        db.scheduleDao().upsert(WeeklySchedule(timestamp = timestamp, weeklyScheduleRaw = rawSchedule))
        val loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        assertThat(loaded.weeklySchedule.weeklyScheduleRaw, `is`(rawSchedule))
    }

    @Test
    fun insertAndReadDailySchedule() {
        val rawSchedule = TestUtils.createWeeklyScheduleOneWeek()
        val weeklySchedule = WeeklySchedule(timestamp = 1L, weeklyScheduleRaw = rawSchedule)
        db.scheduleDao().upsert(weeklySchedule)

        val dailySchedules = arrayListOf<DailySchedule>()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().insertDailySchedules(dailySchedules)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.dailySchedule.id, `is`(date))
        }
    }

    @Test
    fun upsertAndReadDailySchedule() {
        var rawSchedule = TestUtils.createWeeklyScheduleOneWeek()
        val weeklySchedule = WeeklySchedule(timestamp = 1L, weeklyScheduleRaw = rawSchedule)
        db.scheduleDao().upsert(weeklySchedule)

        var dailySchedules = arrayListOf<DailySchedule>()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().upsertDailySchedules(dailySchedules)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.dailySchedule.id, `is`(date))
        }

        rawSchedule = TestUtils.createWeeklyScheduleTwoWeek()

        dailySchedules = arrayListOf()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().upsertDailySchedules(dailySchedules)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.dailySchedule.id, `is`(date))
        }
    }

    @Test
    fun insertAndReadShows() {
        val rawSchedule = TestUtils.createWeeklyScheduleOneWeek()
        val weeklySchedule = WeeklySchedule(timestamp = 1L, weeklyScheduleRaw = rawSchedule)
        db.scheduleDao().upsert(weeklySchedule)

        val dailySchedules = arrayListOf<DailySchedule>()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().upsertDailySchedules(dailySchedules)

        val showsRoom = arrayListOf<Show>()
        rawSchedule.schedule.forEach { (date, shows) ->
            shows.forEach { show ->
                showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                        show.timeEnd, show.length, show.game, show.youtubeId, show.type))
            }

        }

        db.scheduleDao().insertShows(showsRoom)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.shows.size, not(0))
            loaded.shows.forEach { show ->
                assert(loaded.shows.contains(show))
            }
        }
    }

    @Test
    fun upsertAndReadShows() {
        var rawSchedule = TestUtils.createWeeklyScheduleOneWeek()
        var weeklySchedule = WeeklySchedule(timestamp = 1L, weeklyScheduleRaw = rawSchedule)
        db.scheduleDao().upsert(weeklySchedule)

        var dailySchedules = arrayListOf<DailySchedule>()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().upsertDailySchedules(dailySchedules)

        var showsRoom = arrayListOf<Show>()
        rawSchedule.schedule.forEach { (date, shows) ->
            shows.forEach { show ->
                showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                        show.timeEnd, show.length, show.game, show.youtubeId, show.type))
            }

        }

        db.scheduleDao().insertShows(showsRoom)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.shows.size, not(0))
            loaded.shows.forEach { show ->
                assert(loaded.shows.contains(show))
            }
        }

        rawSchedule = TestUtils.createWeeklyScheduleOneWeek()
        weeklySchedule = WeeklySchedule(timestamp = 1L, weeklyScheduleRaw = rawSchedule)
        db.scheduleDao().upsert(weeklySchedule)

        dailySchedules = arrayListOf()
        rawSchedule.dateKeys.forEach { date ->
            dailySchedules.add(DailySchedule(date, weeklySchedule.id))
        }

        db.scheduleDao().upsertDailySchedules(dailySchedules)

        showsRoom = arrayListOf()
        rawSchedule.schedule.forEach { (date, shows) ->
            shows.forEach { show ->
                showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                        show.timeEnd, show.length, show.game, show.youtubeId, show.type))
            }

        }

        db.scheduleDao().insertShows(showsRoom)

        rawSchedule.dateKeys.forEach { date ->
            val loaded = getValue(db.scheduleDao().getDailyScheduleWithShows(date))
            assertThat(loaded, notNullValue())
            assertThat(loaded.shows.size, not(0))
            loaded.shows.forEach { show ->
                assert(loaded.shows.contains(show))
            }
        }
    }

    @Test
    fun upsertAndMultipleWeeklySchedules() {
        every { clock.nowInMillis() } returns 1L

        var rawSchedule = TestUtils.createWeeklyScheduleOneWeek()

        db.scheduleDao().upsertSchedule(clock, rawSchedule)
        val loaded = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        val calendar = GregorianCalendar()
        calendar.set(2018, Calendar.MAY, 7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        assertThat(loaded, `is`(notNullValue()))
        assertThat(loaded.weeklySchedule.timestamp, `is`(1L))
        assertThat(loaded.dailySchedulesWithShows.size, `is`(7))
        assertThat(DateUtils.truncatedEquals(loaded.getStartDate(), calendar.time, Calendar.YEAR),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(loaded.getStartDate(), calendar.time, Calendar.MONTH),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(loaded.getStartDate(), calendar.time, Calendar.DAY_OF_MONTH),
                `is`(true))

        calendar.set(Calendar.DAY_OF_MONTH, 13)

        assertThat(DateUtils.truncatedEquals(loaded.getEndDate(), calendar.time, Calendar.YEAR),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(loaded.getEndDate(), calendar.time, Calendar.MONTH),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(loaded.getEndDate(), calendar.time, Calendar.DAY_OF_MONTH),
                `is`(true))

        var dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[0]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(16))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[1]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(18))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[2]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(20))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[3]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(18))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[4]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(17))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[5]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(6))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[6]))
        assertThat(dailySchedule, `is`(notNullValue()))
        assertThat(dailySchedule.shows.size, `is`(4))


        //-----------------------------------------------------------
        every { clock.nowInMillis() } returns 2L

        rawSchedule = TestUtils.createWeeklyScheduleTwoWeek()

        db.scheduleDao().upsertSchedule(clock, rawSchedule)
        val updated = getValue(db.scheduleDao().getWeeklyScheduleWithDailySchedules())

        calendar.set(2018, Calendar.MAY, 14)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        assertThat(updated, `is`(notNullValue()))
        assertThat(updated.weeklySchedule.timestamp, `is`(2L))
        assertThat(updated.dailySchedulesWithShows.size, `is`(14))
        assertThat(DateUtils.truncatedEquals(updated.getStartDate(), calendar.time, Calendar.YEAR),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(updated.getStartDate(), calendar.time, Calendar.MONTH),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(updated.getStartDate(), calendar.time, Calendar.DAY_OF_MONTH),
                `is`(true))

        calendar.set(Calendar.DAY_OF_MONTH, 27)

        assertThat(DateUtils.truncatedEquals(updated.getEndDate(), calendar.time, Calendar.YEAR),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(updated.getEndDate(), calendar.time, Calendar.MONTH),
                `is`(true))
        assertThat(DateUtils.truncatedEquals(updated.getEndDate(), calendar.time, Calendar.DAY_OF_MONTH),
                `is`(true))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[0]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[1]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[2]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[3]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[4]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[5]))
        assertThat(dailySchedule, `is`(nullValue()))

        dailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(loaded
                .dailySchedulesWithShows[6]))
        assertThat(dailySchedule, `is`(nullValue()))

        var updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[0]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(18))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[1]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(21))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[2]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(13))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[3]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(13))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[4]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(15))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[5]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(15))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[6]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(18))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[7]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(17))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[8]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(16))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[9]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(19))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[10]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(18))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[11]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(12))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[12]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(8))

        updatedDailySchedule = getValue(db.scheduleDao().getDailyScheduleWithShows(updated
                .dailySchedulesWithShows[13]))
        assertThat(updatedDailySchedule, `is`(notNullValue()))
        assertThat(updatedDailySchedule.shows.size, `is`(4))
    }
}