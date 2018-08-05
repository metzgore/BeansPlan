package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderDeletionOrUpdateDialogFragment

@Module(subcomponents = [(ReminderDeletionOrUpdateDialogFragmentSubComponent::class)])
abstract class ReminderDeletionOrUpdateDialogFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(ReminderDeletionOrUpdateDialogFragment::class)

    abstract fun bindReminderDeletionOrUpdateDialogFragment(builder: ReminderDeletionOrUpdateDialogFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}