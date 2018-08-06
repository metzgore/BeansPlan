package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment

@Module(subcomponents = [(ReminderTimePickerDialogFragmentSubComponent::class)])
abstract class ReminderTimePickerDialogFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(ReminderTimePickerDialogFragment::class)

    abstract fun bindReminderTimePickerDialogFragment(builder: ReminderTimePickerDialogFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}