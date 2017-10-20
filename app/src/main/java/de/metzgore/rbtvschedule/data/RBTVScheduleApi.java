package de.metzgore.rbtvschedule.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RBTVScheduleApi {
    @GET("/api/1.0/schedule/schedule.json")
    Call<WeeklySchedule> scheduleOfCurrentWeek();

    @GET("/api/1.0/schedule/{year}/{month}/{day}.json")
    Call<Schedule> scheduleOfDay(@Path("year") int year, @Path("month") int month, @Path("day") String day);
}
