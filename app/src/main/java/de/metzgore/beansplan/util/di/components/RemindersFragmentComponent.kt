package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.reminders.RemindersFragment


@Module(subcomponents = [(RemindersFragmentSubComponent::class)])
abstract class RemindersFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(RemindersFragment::class)
    abstract fun bindRemindersFragment(builder: RemindersFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}