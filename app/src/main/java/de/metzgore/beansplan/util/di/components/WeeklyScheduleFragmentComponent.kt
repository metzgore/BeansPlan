package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment

@Module(subcomponents = [(WeeklyScheduleFragmentSubComponent::class)])
abstract class WeeklyScheduleFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(WeeklyScheduleFragment::class)
    abstract fun bindWeeklyScheduleFragment(builder: WeeklyScheduleFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}
