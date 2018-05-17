package de.metzgore.beansplan.util.di.modules;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.BuildConfig;
import de.metzgore.beansplan.api.RbtvScheduleApi;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Show;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.util.LiveDataCallAdapterFactory;
import io.gsonfire.GsonFireBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;

@Module
public class RbtvApiModule {

    @Provides
    @Singleton
    public RbtvScheduleApi providesRbtvScheduleApi(Retrofit retrofit) {
        return retrofit.create(RbtvScheduleApi.class);
    }

    @Provides
    @Singleton
    public Retrofit retrofit(GsonConverterFactory gsonConverterFactory, LiveDataCallAdapterFactory
            liveDataCallAdapterFactory) {
        return new Retrofit.Builder().baseUrl(BuildConfig.HOST).addConverterFactory
                (gsonConverterFactory).addCallAdapterFactory(liveDataCallAdapterFactory).build();
    }

    @Provides
    @Singleton
    public GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonFireBuilder().enableHooks(Show.class).enableHooks(DailySchedule.class)
                .enableHooks(WeeklySchedule.class).createGsonBuilder().setDateFormat
                        ("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM.yyyy").create();
    }

    @Provides
    @Singleton
    public LiveDataCallAdapterFactory liveDataCallAdapterFactory() {
        return new LiveDataCallAdapterFactory();
    }
}
