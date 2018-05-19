package de.metzgore.beansplan.util.di.modules;

import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment;

@Module
public class RefreshableDailyScheduleFragmentModule {

    @Provides
    RefreshableDailyScheduleFragment provideRefreshableDailyScheduleFragment(RefreshableDailyScheduleFragment
                                                                                     fragment) {
        return fragment;
    }
}
