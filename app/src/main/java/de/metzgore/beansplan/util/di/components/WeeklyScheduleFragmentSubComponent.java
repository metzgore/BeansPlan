package de.metzgore.beansplan.util.di.components;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.metzgore.beansplan.weeklyschedule.WeeklyScheduleFragment;

@Subcomponent
public interface WeeklyScheduleFragmentSubComponent extends AndroidInjector<WeeklyScheduleFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<WeeklyScheduleFragment> {
    }
}

