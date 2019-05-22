package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.MainActivity

@Module(subcomponents = [(MainActivitySubComponent::class)])
abstract class MainActivityComponent {

    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    abstract fun bindMainActivity(builder: MainActivitySubComponent.Builder): AndroidInjector.Factory<*>

}
