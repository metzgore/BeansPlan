package de.metzgore.rbtvschedule.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RBTVScheduleApi {
    @GET("/api/1.0/schedule/{year}/{month}/{day}.json")
    Call<Schedule> dailySchedule(@Path("year") String year,
                                 @Path("month") String month,
                                 @Path("day") String day);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.rbtv.rodney.io/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
}
