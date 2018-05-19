package de.metzgore.beansplan.util.di.components;

import android.support.v4.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;
import de.metzgore.beansplan.dailyschedule.RefreshableDailyScheduleFragment;

@Module(subcomponents = RefreshableDailyScheduleFragmentSubComponent.class)
public abstract class RefreshableDailyScheduleFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(RefreshableDailyScheduleFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindRefreshableDailyScheduleFragment
            (RefreshableDailyScheduleFragmentSubComponent.Builder builder);

}
