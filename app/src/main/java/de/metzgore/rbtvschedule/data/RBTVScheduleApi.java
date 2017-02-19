package de.metzgore.rbtvschedule.data;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RBTVScheduleApi {
    @GET("/api/1.0/schedule/schedule.json")
    Call<Schedule> weeklySchedule();
}
