package de.metzgore.beansplan.util.di.components;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import de.metzgore.beansplan.BeansPlanApp;
import de.metzgore.beansplan.util.di.modules.ContextModule;
import de.metzgore.beansplan.util.di.modules.DailyScheduleDaoModule;
import de.metzgore.beansplan.util.di.modules.MainActivityComponent;
import de.metzgore.beansplan.util.di.modules.RepositoryModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidInjectionModule.class,
        RepositoryModule.class,
        DailyScheduleDaoModule.class,
        ContextModule.class,
        MainActivityComponent.class})
public interface AppComponent {

    void inject(BeansPlanApp app);
}
