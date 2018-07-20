package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment

@Module(subcomponents = [(ReminderDeletionDialogFragmentSubComponent::class)])
abstract class ReminderDeletionDialogFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(ReminderDeletionDialogFragment::class)

    abstract fun bindReminderDeletionDialogFragment(builder: ReminderDeletionDialogFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}