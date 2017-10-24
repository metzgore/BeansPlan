package de.metzgore.rbtvschedule.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import de.metzgore.rbtvschedule.RBTVScheduleApp;
import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
                .addInterceptor(provideCachingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .cache(provideCache())
                .build();
    }

    private static Interceptor provideCachingInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build();

            return response.newBuilder().addHeader("Cache-Control", cacheControl.toString()).build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            if (!RBTVScheduleApp.hasNetwork()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

    private static Cache provideCache() {
        return new Cache(new File(RBTVScheduleApp.getAppContext().getCacheDir(), "http-cache"),
                1024*1024*5);
    }
}
