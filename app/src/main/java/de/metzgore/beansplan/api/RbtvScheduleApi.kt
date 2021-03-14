package de.metzgore.beansplan.api

import androidx.lifecycle.LiveData
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RbtvScheduleApi {
    @GET("/v1/schedule/normalized")
    fun getScheduleFromTimestamp(@Query("startDay") startDay: Int): LiveData<ApiResponse<WeeklyScheduleResponse>>
}
