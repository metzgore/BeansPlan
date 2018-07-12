package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.dailyschedule.DailyScheduleFragment

@Subcomponent
interface DailyScheduleFragmentSubComponent : AndroidInjector<DailyScheduleFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DailyScheduleFragment>()
}