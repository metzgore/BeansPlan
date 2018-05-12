package de.metzgore.beansplan.util.di;

import com.google.gson.Gson;
import de.metzgore.beansplan.AppExecutors;
import de.metzgore.beansplan.BuildConfig;
import de.metzgore.beansplan.api.RbtvScheduleApi;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Show;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.util.LiveDataCallAdapterFactory;
import io.gsonfire.GsonFireBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injector {

    public static AppExecutors provideAppExecutors() {
        return new AppExecutors();
    }

    public static RbtvScheduleApi provideRbtvScheduleApi() {
        return provideRetrofit().create(RbtvScheduleApi.class);
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder().baseUrl(BuildConfig.HOST).addConverterFactory
                (provideGsonConverterFactory()).addCallAdapterFactory(new
                LiveDataCallAdapterFactory()).build();
    }

    private static GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create(provideGson());
    }

    public static Gson provideGson() {
        return new GsonFireBuilder().enableHooks(Show.class).enableHooks(DailySchedule.class)
                .enableHooks(WeeklySchedule.class).createGsonBuilder().setDateFormat
                        ("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM.yyyy").create();
    }
}
