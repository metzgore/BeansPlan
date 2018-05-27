package de.metzgore.beansplan.util.di.components

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.MainActivity

@Module(subcomponents = [(MainActivitySubComponent::class)])
abstract class MainActivityComponent {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    abstract fun bindMainActivity(builder: MainActivitySubComponent.Builder): AndroidInjector.Factory<out Activity>

}
