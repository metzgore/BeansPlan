package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment

@Module
class FragmentModule {

    @Provides
    fun provideWeeklyScheduleFragment(fragment: WeeklyScheduleFragment): WeeklyScheduleFragment {
        return fragment
    }
}
