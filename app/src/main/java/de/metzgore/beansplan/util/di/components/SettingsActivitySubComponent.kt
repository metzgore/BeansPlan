package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.settings.SettingsActivity

@Subcomponent
interface SettingsActivitySubComponent : AndroidInjector<SettingsActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsActivity>()
}