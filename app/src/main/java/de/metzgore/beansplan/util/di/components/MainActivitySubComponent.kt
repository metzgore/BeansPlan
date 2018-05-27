package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.MainActivity

@Subcomponent
interface MainActivitySubComponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}
