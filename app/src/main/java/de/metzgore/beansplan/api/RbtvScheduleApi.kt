package de.metzgore.beansplan.api

import android.arch.lifecycle.LiveData
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import retrofit2.http.GET

interface RbtvScheduleApi {
    @GET("/v1/schedule/legacy/weekdays")
    fun scheduleOfCurrentWeek(): LiveData<ApiResponse<WeeklyScheduleResponse>>
}
