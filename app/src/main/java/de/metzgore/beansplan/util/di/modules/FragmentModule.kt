package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.dailyschedule.DailyScheduleFragment
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment

@Module
class FragmentModule {

    @Provides
    fun provideDailyScheduleFragment(fragment: DailyScheduleFragment): DailyScheduleFragment {
        return fragment
    }

    @Provides
    fun provideWeeklyScheduleFragment(fragment: WeeklyScheduleFragment): WeeklyScheduleFragment {
        return fragment
    }
}
