package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderDeletionOrUpdateDialogFragment

@Module(subcomponents = [(ReminderDeletionOrUpdateDialogFragmentSubComponent::class)])
abstract class ReminderDeletionOrUpdateDialogFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(ReminderDeletionOrUpdateDialogFragment::class)
    abstract fun bindReminderDeletionOrUpdateDialogFragment(builder: ReminderDeletionOrUpdateDialogFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}