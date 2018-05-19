package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment

@Subcomponent
interface RefreshableDailyScheduleFragmentSubComponent : AndroidInjector<RefreshableDailyScheduleFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RefreshableDailyScheduleFragment>()
}
