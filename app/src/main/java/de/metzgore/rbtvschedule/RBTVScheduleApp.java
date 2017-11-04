package de.metzgore.rbtvschedule;

import android.app.Application;
import android.content.Context;

public class RBTVScheduleApp extends Application {

    private static RBTVScheduleApp mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
    }

    //TODO dagger
    public static Context getAppContext() {
        return mAppContext.getApplicationContext();
    }
}
