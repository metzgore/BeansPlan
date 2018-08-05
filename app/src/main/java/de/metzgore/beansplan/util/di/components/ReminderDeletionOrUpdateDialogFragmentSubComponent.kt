package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.shared.ReminderDeletionOrUpdateDialogFragment

@Subcomponent
interface ReminderDeletionOrUpdateDialogFragmentSubComponent : AndroidInjector<ReminderDeletionOrUpdateDialogFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReminderDeletionOrUpdateDialogFragment>()
}