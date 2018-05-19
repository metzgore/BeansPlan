package de.metzgore.beansplan.util.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.settings.repository.AppSettings
import de.metzgore.beansplan.settings.repository.AppSettingsImp

import javax.inject.Singleton

@Module(includes = [(ContextModule::class)])
class SettingsModule {

    @Provides
    @Singleton
    fun providesAppSettings(context: Context): AppSettings {
        return AppSettingsImp(context)
    }
}
