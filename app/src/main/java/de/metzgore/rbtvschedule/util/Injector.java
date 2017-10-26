package de.metzgore.rbtvschedule.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injector {

    public static RBTVScheduleApi provideRBTVScheduleApi() {
        return provideRetrofit().create(RBTVScheduleApi.class);
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://api.rbtv.rodney.io/")
                .client(provideOkHttpClient())
                .addConverterFactory(provideGsonConverterFactory())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }

    private static GsonConverterFactory provideGsonConverterFactory() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setDateFormat("dd.MM.yyyy")
                .create();

        return GsonConverterFactory.create(gson);
    }

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }
}
