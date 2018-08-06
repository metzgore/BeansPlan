package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.dailyschedule.DailyScheduleFragment

@Module(subcomponents = [(DailyScheduleFragmentSubComponent::class)])
abstract class DailyScheduleFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(DailyScheduleFragment::class)
    abstract fun bindDailyScheduleFragment(builder: DailyScheduleFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}