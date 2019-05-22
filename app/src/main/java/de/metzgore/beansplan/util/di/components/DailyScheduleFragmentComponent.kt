package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.dailyschedule.DailyScheduleFragment

@Module(subcomponents = [(DailyScheduleFragmentSubComponent::class)])
abstract class DailyScheduleFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(DailyScheduleFragment::class)
    abstract fun bindDailyScheduleFragment(builder: DailyScheduleFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}