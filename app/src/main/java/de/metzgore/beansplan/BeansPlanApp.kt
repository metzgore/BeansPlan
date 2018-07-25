package de.metzgore.beansplan

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import de.metzgore.beansplan.notifications.NotificationHelper
import de.metzgore.beansplan.util.di.components.AppComponent
import de.metzgore.beansplan.util.di.components.DaggerAppComponent
import de.metzgore.beansplan.util.di.modules.ContextModule
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule
import javax.inject.Inject

class BeansPlanApp : Application(), HasActivityInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var broadcastDispatchingAndroidInjector: DispatchingAndroidInjector<BroadcastReceiver>

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(applicationContext)

        appComponent = createAppComponent()
        appComponent.inject(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun createAppComponent(): AppComponent {
        return DaggerAppComponent.builder().apply {
            scheduleDaoModule(ScheduleDaoModule(false))
            contextModule(ContextModule(applicationContext))
        }.build()
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return broadcastDispatchingAndroidInjector
    }
}
