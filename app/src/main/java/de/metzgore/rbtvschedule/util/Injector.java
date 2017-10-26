package de.metzgore.rbtvschedule.util;

import com.google.gson.Gson;

import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.Show;
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

    public static GsonConverterFactory provideGsonConverterFactory() {
        Gson gson = new GsonFireBuilder()
                .enableHooks(Show.class)
                .createGsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setDateFormat("dd.MM.yyyy")
                .create();

        return GsonConverterFactory.create(gson);
    }
}
