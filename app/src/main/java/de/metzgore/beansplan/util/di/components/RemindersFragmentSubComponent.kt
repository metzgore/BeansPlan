package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.reminders.RemindersFragment

@Subcomponent
interface RemindersFragmentSubComponent : AndroidInjector<RemindersFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RemindersFragment>()
}
