package de.metzgore.beansplan.util.di.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.settings.repository.AppSettings;
import de.metzgore.beansplan.settings.repository.AppSettingsImp;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class SettingsModule {

    @Provides
    @Singleton
    public AppSettings providesAppSettings(Context context) {
        return new AppSettingsImp(context);
    }
}
