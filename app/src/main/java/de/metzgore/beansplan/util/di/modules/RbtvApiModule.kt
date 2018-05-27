package de.metzgore.beansplan.util.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.BuildConfig
import de.metzgore.beansplan.api.RbtvScheduleApi
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Show
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.util.LiveDataCallAdapterFactory
import io.gsonfire.GsonFireBuilder
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
    fun retrofit(gsonConverterFactory: GsonConverterFactory, liveDataCallAdapterFactory: LiveDataCallAdapterFactory): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.HOST).addConverterFactory(gsonConverterFactory).addCallAdapterFactory(liveDataCallAdapterFactory).build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonFireBuilder().enableHooks(Show::class.java).enableHooks(DailySchedule::class.java)
                .enableHooks(WeeklySchedule::class.java).createGsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM.yyyy").create()
    }

    @Provides
    @Singleton
    fun liveDataCallAdapterFactory(): LiveDataCallAdapterFactory {
        return LiveDataCallAdapterFactory()
    }
}
