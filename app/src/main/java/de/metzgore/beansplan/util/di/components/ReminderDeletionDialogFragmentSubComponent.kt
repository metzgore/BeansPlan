package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.shared.ReminderDeletionDialogFragment

@Subcomponent
interface ReminderDeletionDialogFragmentSubComponent : AndroidInjector<ReminderDeletionDialogFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReminderDeletionDialogFragment>()
}