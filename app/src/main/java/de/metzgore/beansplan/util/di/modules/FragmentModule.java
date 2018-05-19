package de.metzgore.beansplan.util.di.modules;

import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment;

@Module
public class FragmentModule {

    @Provides
    RefreshableDailyScheduleFragment provideRefreshableDailyScheduleFragment(RefreshableDailyScheduleFragment
                                                                                     fragment) {
        return fragment;
    }

    @Provides
    WeeklyScheduleFragment provideWeeklyScheduleFragment(WeeklyScheduleFragment fragment) {
        return fragment;
    }
}
