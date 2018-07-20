package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.shared.ReminderTimePickerDialogFragment

@Subcomponent
interface ReminderTimePickerDialogFragmentSubComponent : AndroidInjector<ReminderTimePickerDialogFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReminderTimePickerDialogFragment>()
}