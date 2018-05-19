package de.metzgore.beansplan.util.di.components;

import android.app.Activity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import de.metzgore.beansplan.MainActivity;
import de.metzgore.beansplan.util.di.components.MainActivitySubComponent;

@Module(subcomponents = MainActivitySubComponent.class)
public abstract class MainActivityComponent {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivity(MainActivitySubComponent.Builder builder);

}
