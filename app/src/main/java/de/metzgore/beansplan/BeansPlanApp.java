package de.metzgore.beansplan;

import android.app.Activity;
import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.metzgore.beansplan.util.di.components.DaggerAppComponent;
import de.metzgore.beansplan.util.di.modules.ContextModule;
import de.metzgore.beansplan.util.di.modules.DailyScheduleDaoModule;
import de.metzgore.beansplan.util.di.modules.RbtvApiModule;
import de.metzgore.beansplan.util.di.modules.RepositoryModule;

import javax.inject.Inject;

public class BeansPlanApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        createAppComponent();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    private void createAppComponent() {
        DaggerAppComponent.builder().dailyScheduleDaoModule(new DailyScheduleDaoModule(false))
                .repositoryModule(new RepositoryModule())
                .rbtvApiModule(new RbtvApiModule())
                .contextModule(new ContextModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }
}
