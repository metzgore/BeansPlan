package de.metzgore.beansplan.api

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.data.WeeklySchedule
import retrofit2.http.GET

interface RbtvScheduleApi {
    @GET("/api/1.0/schedule/schedule.json")
    fun scheduleOfCurrentWeek(): LiveData<ApiResponse<WeeklySchedule>>
}
