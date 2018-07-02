package de.metzgore.beansplan.util.di.modules

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.BuildConfig
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.util.LiveDataCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class RbtvApiModule {

    @Provides
    @Singleton
    fun providesRbtvScheduleApi(retrofit: Retrofit): RbtvScheduleApi {
        return retrofit.create(RbtvScheduleApi::class.java)
    }

    @Provides
    @Singleton
    fun retrofit(gsonConverterFactory: GsonConverterFactory, liveDataCallAdapterFactory:
    LiveDataCallAdapterFactory, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.HOST).client(okHttpClient)
                .addConverterFactory(gsonConverterFactory).addCallAdapterFactory(liveDataCallAdapterFactory).build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM.yyyy").create()
    }

    @Provides
    @Singleton
    fun liveDataCallAdapterFactory(): LiveDataCallAdapterFactory {
        return LiveDataCallAdapterFactory()
    }

    @Provides
    @Singleton
    fun okhttpClient(context: Context): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        builder.cache(
                Cache(context.cacheDir, 1024 * 1024 * 10))
                .addInterceptor(logging)
        return builder.build()
    }
}
