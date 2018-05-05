package de.metzgore.rbtvschedule.util.di;

import com.google.gson.Gson;

import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.DailySchedule;
import de.metzgore.rbtvschedule.data.Show;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.util.LiveDataCallAdapterFactory;
import io.gsonfire.GsonFireBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injector {

    public static RBTVScheduleApi provideRBTVScheduleApi() {
        return provideRetrofit().create(RBTVScheduleApi.class);
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://api.rbtv.rodney.io/")
                .addConverterFactory(provideGsonConverterFactory())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }

    private static GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create(provideGson());
    }

    public static Gson provideGson() {
        return new GsonFireBuilder()
                .enableHooks(Show.class)
                .enableHooks(DailySchedule.class)
                .enableHooks(WeeklySchedule.class)
                .createGsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setDateFormat("dd.MM.yyyy")
                .create();
    }
}
