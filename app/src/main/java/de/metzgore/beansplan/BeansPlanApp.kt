package de.metzgore.beansplan

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import de.metzgore.beansplan.notifications.NotificationHelper
import de.metzgore.beansplan.util.di.components.AppComponent
import de.metzgore.beansplan.util.di.components.DaggerAppComponent
import de.metzgore.beansplan.util.di.modules.ContextModule
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule
import javax.inject.Inject

class BeansPlanApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(applicationContext)

        appComponent = createAppComponent()
        appComponent.inject(this)
    }

    private fun createAppComponent(): AppComponent {
        return DaggerAppComponent.builder().apply {
            scheduleDaoModule(ScheduleDaoModule(false))
            contextModule(ContextModule(applicationContext))
        }.build()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityDispatchingAndroidInjector
    }
}
