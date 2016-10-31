package de.metzgore.rbtvschedule.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
}
