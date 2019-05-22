package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.settings.SettingsActivity

@Module(subcomponents = [(SettingsActivitySubComponent::class)])
abstract class SettingsActivityComponent {

    @Binds
    @IntoMap
    @ClassKey(SettingsActivity::class)
    abstract fun bindSettingsActivity(builder: SettingsActivitySubComponent.Builder): AndroidInjector.Factory<*>

}