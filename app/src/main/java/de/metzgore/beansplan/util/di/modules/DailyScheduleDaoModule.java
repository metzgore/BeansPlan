package de.metzgore.beansplan.util.di.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.DailyScheduleDaoImpl;
import de.metzgore.beansplan.shared.DailyScheduleDao;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class DailyScheduleDaoModule {

    private boolean cacheOnly;

    public DailyScheduleDaoModule(boolean cacheOnly) {
        this.cacheOnly = cacheOnly;
    }

    @Provides
    @Singleton
    public DailyScheduleDao providesDailyScheduleDao(Context context) {
        return new DailyScheduleDaoImpl(context, cacheOnly);
    }
}
