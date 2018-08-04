package de.metzgore.beansplan

import TestUtils
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.ShowResponse
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.data.room.DailySchedule
import de.metzgore.beansplan.data.room.Reminder
import de.metzgore.beansplan.data.room.Show
import de.metzgore.beansplan.data.room.WeeklySchedule
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.data.room.relations.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.util.ClockImpl
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }

    fun createFilledDailyScheduleWithShowsLiveData(): LiveData<DailyScheduleWithShows> {
        val dailySchedule = TestUtils.createDailySchedule()
        val scheduleWithShows = DailyScheduleWithShows()

        val dailyScheduleRoom = DailySchedule(dailySchedule.date!!,
                "foo")

        scheduleWithShows.dailySchedule = dailyScheduleRoom

        val showsRoom = arrayListOf<ShowWithReminder>()
        dailySchedule.shows.forEach { show ->
            val showWithReminder = ShowWithReminder()
            showWithReminder.show = Show(show.id, dailySchedule.date!!, show.title, show.topic, show.timeStart,
                    show.timeEnd, show.length, show.game, show.youtubeId, show.type, false)
            showsRoom.add(showWithReminder)
        }

        scheduleWithShows.shows = showsRoom

        val liveData = MutableLiveData<DailyScheduleWithShows>()
        liveData.postValue(scheduleWithShows)

        return liveData
    }

    fun createEmptyDailyScheduleWithShowsLiveData(): LiveData<DailyScheduleWithShows> {
        val dailySchedule = TestUtils.createDailySchedule()
        val scheduleWithShows = DailyScheduleWithShows()

        val dailyScheduleRoom = DailySchedule(dailySchedule.date!!,
                "foo")

        scheduleWithShows.dailySchedule = dailyScheduleRoom
        scheduleWithShows.shows = emptyList()

        val liveData = MutableLiveData<DailyScheduleWithShows>()
        liveData.value = scheduleWithShows

        return liveData
    }

    fun createShowWithReminderLiveData(): LiveData<List<ShowWithReminder>> {
        val showWithReminder = ShowWithReminder()
        showWithReminder.show = Show(1, Date(), "", "", Date(), Date(), 0, "", "", ShowResponse.Type.LIVE, false, 1)
        showWithReminder.reminder = listOf(Reminder(1, Date()))

        val liveData = MutableLiveData<List<ShowWithReminder>>()
        liveData.value = listOf(showWithReminder)

        return liveData
    }

    fun createFilledWeeklyScheduleWithDailySchedulesResourceLiveData(status: Status, forceRefresh: Boolean):
            LiveData<Resource<WeeklyScheduleWithDailySchedules>> {

        val clock = ClockImpl()
        val weeklyScheduleResponse = TestUtils.createWeeklyScheduleOneWeek()

        val weeklySchedule = WeeklySchedule(timestamp =
        clock.nowInMillis(), weeklyScheduleRaw = weeklyScheduleResponse)

        val dailySchedules = arrayListOf<Date>()
        weeklyScheduleResponse.dateKeys.forEach { date ->
            dailySchedules.add(date)
        }

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.weeklySchedule = weeklySchedule
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = dailySchedules

        return createWeeklyScheduleLiveData(weeklyScheduleWithDailySchedules, status, forceRefresh)
    }

    fun createFilledWeeklyScheduleWithDailySchedulesLiveData():
            LiveData<WeeklyScheduleWithDailySchedules> {

        val clock = ClockImpl()
        val weeklyScheduleResponse = TestUtils.createWeeklyScheduleOneWeek()

        val weeklySchedule = WeeklySchedule(timestamp =
        clock.nowInMillis(), weeklyScheduleRaw = weeklyScheduleResponse)

        val dailySchedules = arrayListOf<Date>()
        weeklyScheduleResponse.dateKeys.forEach { date ->
            dailySchedules.add(date)
        }

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.weeklySchedule = weeklySchedule
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = dailySchedules

        val liveData = MutableLiveData<WeeklyScheduleWithDailySchedules>()
        liveData.value = weeklyScheduleWithDailySchedules

        return liveData
    }

    fun createEmptyWeeklyScheduleWithDailySchedulesResourceLiveData(status: Status, forceRefresh: Boolean): LiveData<Resource<WeeklyScheduleWithDailySchedules>> {
        val clock = ClockImpl()
        val weeklyScheduleResponse = TestUtils.createWeeklyScheduleOneWeek()

        val weeklySchedule = WeeklySchedule(timestamp =
        clock.nowInMillis(), weeklyScheduleRaw = weeklyScheduleResponse)

        val weeklyScheduleWithDailySchedules = WeeklyScheduleWithDailySchedules()
        weeklyScheduleWithDailySchedules.weeklySchedule = weeklySchedule
        weeklyScheduleWithDailySchedules.dailySchedulesWithShows = emptyList()

        return createWeeklyScheduleLiveData(weeklyScheduleWithDailySchedules, status, forceRefresh)
    }

    private fun createWeeklyScheduleLiveData(schedule: WeeklyScheduleWithDailySchedules, status: Status, forceRefresh: Boolean): LiveData<Resource<WeeklyScheduleWithDailySchedules>> {
        val liveData = MutableLiveData<Resource<WeeklyScheduleWithDailySchedules>>()

        liveData.value = when (status) {
            Status.SUCCESS -> Resource.success(schedule, forceRefresh)
            Status.ERROR -> Resource.error("foo", schedule, forceRefresh)
            Status.LOADING -> Resource.loading(schedule, forceRefresh)
        }

        return liveData
    }
}