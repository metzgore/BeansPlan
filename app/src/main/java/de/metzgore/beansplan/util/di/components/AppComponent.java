package de.metzgore.beansplan.util.di.components;

import dagger.Component;
import de.metzgore.beansplan.MainActivity;
import de.metzgore.beansplan.util.di.modules.RepositoryModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = RepositoryModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);
}
