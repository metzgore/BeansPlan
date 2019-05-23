package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.settings.SettingsFragment

@Module(subcomponents = [(SettingsFragmentSubComponent::class)])
abstract class SettingsFragmentComponent {

    @Binds
    @IntoMap
    @ClassKey(SettingsFragment::class)
    abstract fun bindSettingsFragment(builder: SettingsFragmentSubComponent.Builder): AndroidInjector.Factory<*>

}