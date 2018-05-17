package de.metzgore.beansplan;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import de.metzgore.beansplan.util.di.components.AppComponent;
import de.metzgore.beansplan.util.di.components.DaggerAppComponent;
import de.metzgore.beansplan.util.di.modules.ContextModule;
import de.metzgore.beansplan.util.di.modules.DailyScheduleDaoModule;
import de.metzgore.beansplan.util.di.modules.RepositoryModule;

public class BeansPlanApp extends Application {

    private static BeansPlanApp mAppContext;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = createAppComponent();

        mAppContext = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public AppComponent appComponent() {
        return appComponent;
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder().repositoryModule(new RepositoryModule())
                .contextModule(new ContextModule(getApplicationContext()))
                .repositoryModule(new RepositoryModule())
                .dailyScheduleDaoModule(new DailyScheduleDaoModule(false)).build();
    }

    public static Context getAppContext() {
        return mAppContext.getApplicationContext();
    }
}
