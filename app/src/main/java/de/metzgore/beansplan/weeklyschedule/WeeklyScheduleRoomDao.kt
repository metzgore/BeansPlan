package de.metzgore.beansplan.weeklyschedule

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.data.room.*
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.distinctUntilChanged
import java.util.*


@Dao
abstract class WeeklyScheduleRoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(weeklySchedule: WeeklySchedule): Long

    @Update
    abstract fun update(weeklySchedule: WeeklySchedule): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDailySchedules(schedules: List<DailySchedule>): List<Long>

    @Update
    abstract fun updateDailySchedules(schedules: List<DailySchedule>): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertShows(shows: List<Show>): List<Long>

    @Update
    abstract fun updateShows(shows: List<Show>): Int

    fun upsert(obj: WeeklySchedule) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    fun upsertDailySchedules(objList: List<DailySchedule>) {
        val insertResult = insertDailySchedules(objList)
        val updateList = mutableListOf<DailySchedule>()

        for (i in 0 until insertResult.size) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }

        if (!updateList.isEmpty()) {
            updateDailySchedules(updateList)
        }
    }

    fun upsertShows(objList: List<Show>) {
        val insertResult = insertShows(objList)
        val updateList = mutableListOf<Show>()

        for (i in 0 until insertResult.size) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }

        if (!updateList.isEmpty()) {
            updateShows(updateList)
        }
    }

    @Transaction
    @Query("SELECT * FROM weeklyschedule")
    abstract fun getWeeklyScheduleWithDailySchedules(): LiveData<WeeklyScheduleWithDailySchedules>

    fun getWeeklyScheduleWithDailySchedulesDistinct():
            LiveData<WeeklyScheduleWithDailySchedules> = getWeeklyScheduleWithDailySchedules()
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

    @Query("DELETE FROM show WHERE id NOT IN (:showIds)")
    abstract fun deleteLeftOverShows(showIds: List<Long>)

    @Transaction
    open fun upsertSchedule(clock: Clock, item: WeeklyScheduleResponse) {
        val weeklySchedule = de.metzgore.beansplan.data.room.WeeklySchedule(timestamp =
        clock.nowInMillis(), weeklyScheduleRaw = item)
        upsert(weeklySchedule)

        val dailySchedules = arrayListOf<de.metzgore.beansplan.data.room.DailySchedule>()
        item.dateKeys.forEach { date ->
            dailySchedules.add(de.metzgore
                    .beansplan.data.room.DailySchedule(date, weeklySchedule.id))
        }
        upsertDailySchedules(dailySchedules)
        deleteLeftOverDailySchedule(dailySchedules.map {
            it.id.time
        })

        val showsRoom = arrayListOf<de.metzgore.beansplan.data.room.Show>()
        item.schedule.forEach { (date, shows) ->
            shows.forEach { show ->
                showsRoom.add(Show(show.id, date, show.title, show.topic, show.timeStart,
                        show.timeEnd, show.length, show.game, show.youtubeId, show.type))
            }

        }
        upsertShows(showsRoom)
        deleteLeftOverShows(showsRoom.map {
            it.id
        })
    }
}