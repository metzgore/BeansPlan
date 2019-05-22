package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment

@Module(subcomponents = [(ReminderDeletionDialogFragmentSubComponent::class)])
abstract class ReminderDeletionDialogFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(ReminderDeletionDialogFragment::class)
    abstract fun bindReminderDeletionDialogFragment(builder: ReminderDeletionDialogFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}