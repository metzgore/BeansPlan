package de.metzgore.beansplan.util.di.components

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.settings.SettingsActivity

@Module(subcomponents = [(SettingsActivitySubComponent::class)])
abstract class SettingsActivityComponent {

    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity::class)
    abstract fun bindSettingsActivity(builder: SettingsActivitySubComponent.Builder): AndroidInjector.Factory<out Activity>

}