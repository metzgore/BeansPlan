package de.metzgore.beansplan.util.di.modules;


import dagger.Module;
import dagger.Provides;
import de.metzgore.beansplan.MainActivity;

@Module
public class MainActivityModule {

    @Provides
    MainActivity provideMainActivity(MainActivity mainActivity) {
        return mainActivity;
    }
}
