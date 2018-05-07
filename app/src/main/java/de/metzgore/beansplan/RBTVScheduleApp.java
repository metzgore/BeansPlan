package de.metzgore.beansplan;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

public class RBTVScheduleApp extends Application {

    private static RBTVScheduleApp mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    //TODO dagger
    public static Context getAppContext() {
        return mAppContext.getApplicationContext();
    }
}
