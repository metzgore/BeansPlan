package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.reminders.RemindersFragment


@Module(subcomponents = [(RemindersFragmentSubComponent::class)])
abstract class RemindersFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(RemindersFragment::class)
    abstract fun bindRemindersFragment(builder: RemindersFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}