package de.metzgore.beansplan.util.di.components;

import dagger.Component;
import de.metzgore.beansplan.BeansPlanApp;
import de.metzgore.beansplan.util.di.modules.ContextModule;
import de.metzgore.beansplan.util.di.modules.RepositoryModule;
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule;
import de.metzgore.beansplan.util.di.modules.SettingsModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        RepositoryModule.class,
        ScheduleDaoModule.class,
        ContextModule.class,
        MainActivityComponent.class,
        RefreshableDailyScheduleFragmentComponent.class,
        WeeklyScheduleFragmentComponent.class,
        SettingsModule.class})
public interface AppComponent {

    void inject(BeansPlanApp app);
}
