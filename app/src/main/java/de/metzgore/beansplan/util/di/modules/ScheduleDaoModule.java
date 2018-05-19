package de.metzgore.beansplan.util.di.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.DailyScheduleDaoImpl;
import de.metzgore.beansplan.shared.DailyScheduleDao;
import de.metzgore.beansplan.shared.WeeklyScheduleDao;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleDaoImpl;

import javax.inject.Singleton;

@Module(includes = ContextModule.class)
public class ScheduleDaoModule {

    private boolean cacheOnly;

    public ScheduleDaoModule(boolean cacheOnly) {
        this.cacheOnly = cacheOnly;
    }

    @Provides
    @Singleton
    public DailyScheduleDao providesDailyScheduleDao(Context context) {
        return new DailyScheduleDaoImpl(context, cacheOnly);
    }

    @Provides
    @Singleton
    public WeeklyScheduleDao providesWeeklyScheduleDao(Context context) {
        return new WeeklyScheduleDaoImpl(context, cacheOnly);
    }
}
