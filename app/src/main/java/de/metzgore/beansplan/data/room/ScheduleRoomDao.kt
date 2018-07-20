package de.metzgore.beansplan.data.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.data.room.relations.DailyScheduleWithShows
import de.metzgore.beansplan.data.room.relations.ShowWithReminder
import de.metzgore.beansplan.data.room.relations.WeeklyScheduleWithDailySchedules
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.distinctUntilChanged
import java.util.*

@Dao
abstract class ScheduleRoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(weeklySchedule: WeeklySchedule): Long

    @Update
    abstract fun update(weeklySchedule: WeeklySchedule): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDailySchedules(vararg schedules: DailySchedule): List<Long>

    @Update
    abstract fun updateDailySchedules(vararg schedules: DailySchedule): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertShows(vararg show: Show): List<Long>

    @Update
    abstract fun updateShows(vararg show: Show): Int

    fun upsert(obj: WeeklySchedule) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    fun upsertDailySchedules(objList: List<DailySchedule>) {
        val insertResult = insertDailySchedules(*objList.toTypedArray())
        val updateList = mutableListOf<DailySchedule>()

        for (i in 0 until insertResult.size) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }

        if (!updateList.isEmpty()) {
            updateDailySchedules(*updateList.toTypedArray())
        }
    }

    fun upsertShows(objList: List<Show>) {
        val insertResult = insertShows(*objList.toTypedArray())
        val updateList = mutableListOf<Show>()

        for (i in 0 until insertResult.size) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }

        if (!updateList.isEmpty()) {
            updateShows(*updateList.toTypedArray())
        }
    }

    @Transaction
    @Query("SELECT * FROM weeklyschedule")
    abstract fun getWeeklyScheduleWithDailySchedules(): LiveData<WeeklyScheduleWithDailySchedules>

    fun getWeeklyScheduleWithDailySchedulesDistinct():
            LiveData<WeeklyScheduleWithDailySchedules> = getWeeklyScheduleWithDailySchedules()
            .distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM show WHERE reminderId NOT NULL")
    abstract fun getShowsWithReminders(): LiveData<List<ShowWithReminder>>

    @Transaction
    @Query("SELECT * FROM show WHERE reminderId NOT NULL")
    abstract fun getShowsWithRemindersAsList(): List<ShowWithReminder>

    fun getShowsWithRemindersDistict(): LiveData<List<ShowWithReminder>> = getShowsWithReminders()
            .distinctUntilChanged()

    @Transaction
    @Query("SELECT * FROM dailyschedule WHERE id = :id")
    abstract fun getDailyScheduleWithShows(id: Date): LiveData<DailyScheduleWithShows>

    fun getDailyScheduleWithShowsDistinct(id: Date):
            LiveData<DailyScheduleWithShows> = getDailyScheduleWithShows(id)
            .distinctUntilChanged()

    @Query("SELECT weeklyScheduleRaw FROM weeklyschedule")
    abstract fun getWeeklyScheduleResponse(): WeeklyScheduleResponse?

    //TODO is it possible to use TypeConverter for this?
    @Query("DELETE FROM dailyschedule WHERE id NOT IN (:dailyScheduleIds)")
    abstract fun deleteLeftOverDailySchedule(dailyScheduleIds: List<Long>)

    @Query("UPDATE show SET deleted = 1 WHERE id NOT IN (:showIds)")
    abstract fun markShowsDeleted(vararg showIds: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertReminder(reminder: Reminder): Long

    @Update
    abstract fun updateReminder(reminder: Reminder): Int

    @Delete
    abstract fun deleteReminder(reminder: Reminder): Int

    @Transaction
    open fun upsertSchedule(clock: Clock, item: WeeklyScheduleResponse) {
        val weeklySchedule = WeeklySchedule(timestamp =
        clock.nowInMillis(), weeklyScheduleRaw = item)
        upsert(weeklySchedule)

        val dailySchedules = arrayListOf<DailySchedule>()
        item.dateKeys.forEach { date ->
            dailySchedules.add(de.metzgore
                    .beansplan.data.room.DailySchedule(date, weeklySchedule.id))
        }
        upsertDailySchedules(dailySchedules)
        deleteLeftOverDailySchedule(dailySchedules.map {
            it.id.time
        })

        // preserve reminders
        val showsWithReminder = getShowsWithRemindersAsList()

        val showsRoom = arrayListOf<Show>()
        item.schedule.forEach { (date, shows) ->
            shows.forEach { show ->
                val reminder = showsWithReminder.firstOrNull { it.show.id == show.id }

                showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                        show.timeEnd, show.length, show.game, show.youtubeId, show.type, false, reminder?.reminder?.get(0)?.id))
            }
        }
        upsertShows(showsRoom)

        markShowsDeleted(*showsRoom.map {
            it.id
        }.toLongArray())
    }

    @Transaction
    open fun upsertReminder(show: Show, reminder: Reminder) {
        val insertResult = insertReminder(reminder)

        if (insertResult == -1L) {
            updateReminder(reminder)
        }

        show.reminderId = insertResult

        updateShows(show)
    }

    @Transaction
    open fun upsertReminder(showWithReminder: ShowWithReminder) {
        val insertResult = insertReminder(showWithReminder.reminder!![0])

        showWithReminder.show.reminderId = insertResult

        updateShows(showWithReminder.show)
    }

    @Transaction
    open fun deleteReminder(show: Show, reminder: Reminder) {
        show.reminderId = null

        updateShows(show)
        deleteReminder(reminder)
    }

    @Transaction
    open fun deleteReminder(showWithReminder: ShowWithReminder) {
        showWithReminder.show.reminderId = null

        updateShows(showWithReminder.show)
        deleteReminder(showWithReminder.reminder!![0])
    }
}