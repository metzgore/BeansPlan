package de.metzgore.beansplan.util.di.components;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment;

@Subcomponent
public interface RefreshableDailyScheduleFragmentSubComponent extends
        AndroidInjector<RefreshableDailyScheduleFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RefreshableDailyScheduleFragment> {
    }
}
