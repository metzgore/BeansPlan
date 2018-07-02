package de.metzgore.beansplan.util.di.components

import dagger.Component
import de.metzgore.beansplan.BeansPlanApp
import de.metzgore.beansplan.util.di.modules.*

import javax.inject.Singleton

@Singleton
@Component(modules = [(ClockModule::class), (RepositoryModule::class), (ScheduleDaoModule::class)
    , (ContextModule::class), (MainActivityComponent::class),
    (WeeklyScheduleFragmentComponent::class), (DailyScheduleFragmentComponent::class),
    (SettingsModule::class)])
interface AppComponent {

    fun inject(app: BeansPlanApp)
}
