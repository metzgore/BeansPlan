package de.metzgore.beansplan.util.di.components;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.metzgore.beansplan.MainActivity;

@Subcomponent
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}
