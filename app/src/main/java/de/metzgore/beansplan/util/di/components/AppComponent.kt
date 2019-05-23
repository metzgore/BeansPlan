package de.metzgore.beansplan.util.di.components

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import de.metzgore.beansplan.BeansPlanApp
import de.metzgore.beansplan.util.di.modules.ClockModule
import de.metzgore.beansplan.util.di.modules.ContextModule
import de.metzgore.beansplan.util.di.modules.RepositoryModule
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule
import de.metzgore.beansplan.util.di.modules.SettingsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AndroidSupportInjectionModule::class),
    (ClockModule::class), (RepositoryModule::class), (ScheduleDaoModule::class),
    (ContextModule::class), (MainActivityComponent::class), (WeeklyScheduleFragmentComponent::class),
    (DailyScheduleFragmentComponent::class), (RemindersFragmentComponent::class),
    (ReminderDeletionDialogFragmentComponent::class), (ReminderTimePickerDialogFragmentComponent::class),
    (ReminderDeletionOrUpdateDialogFragmentComponent::class), (NotificationPublisherComponent::class),
    (ReminderBootReceiverComponent::class), (SettingsModule::class), (SettingsFragmentComponent::class),
    (SettingsActivityComponent::class)])
interface AppComponent {
    fun inject(app: BeansPlanApp)
}
