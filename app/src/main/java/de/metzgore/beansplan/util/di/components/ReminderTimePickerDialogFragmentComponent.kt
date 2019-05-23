package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment

@Module(subcomponents = [(ReminderTimePickerDialogFragmentSubComponent::class)])
abstract class ReminderTimePickerDialogFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(ReminderTimePickerDialogFragment::class)
    abstract fun bindReminderTimePickerDialogFragment(builder: ReminderTimePickerDialogFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}