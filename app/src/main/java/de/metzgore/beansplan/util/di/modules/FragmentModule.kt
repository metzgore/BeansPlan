package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment

@Module
class FragmentModule {

    @Provides
    fun provideRefreshableDailyScheduleFragment(fragment: RefreshableDailyScheduleFragment): RefreshableDailyScheduleFragment {
        return fragment
    }

    @Provides
    fun provideWeeklyScheduleFragment(fragment: WeeklyScheduleFragment): WeeklyScheduleFragment {
        return fragment
    }
}
