package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment

@Subcomponent
interface WeeklyScheduleFragmentSubComponent : AndroidInjector<WeeklyScheduleFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<WeeklyScheduleFragment>()
}

