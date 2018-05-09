package de.metzgore.beansplan.api

import android.arch.lifecycle.LiveData

import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.WeeklySchedule
import retrofit2.http.GET
import retrofit2.http.Path

interface RbtvScheduleApi {
    @GET("/api/1.0/schedule/schedule.json")
    fun scheduleOfCurrentWeek(): LiveData<ApiResponse<WeeklySchedule>>

    @GET("/api/1.0/schedule/{year}/{month}/{day}.json")
    fun scheduleOfDay(@Path("year") year: Int, @Path("month") month: String, @Path("day") day: String): LiveData<ApiResponse<DailySchedule>>
}
