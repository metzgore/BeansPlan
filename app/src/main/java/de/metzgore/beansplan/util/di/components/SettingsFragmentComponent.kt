package de.metzgore.beansplan.util.di.components

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.settings.SettingsFragment

@Module(subcomponents = [(SettingsFragmentSubComponent::class)])
abstract class SettingsFragmentComponent {

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    abstract fun bindSettingsFragment(builder: SettingsFragmentSubComponent.Builder): AndroidInjector.Factory<out Fragment>

}