package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.notifications.ReminderBootReceiver

@Module(subcomponents = [(ReminderBootReceiverSubComponent::class)])
abstract class ReminderBootReceiverComponent {

    @Binds
    @IntoMap
    @ClassKey(ReminderBootReceiver::class)
    abstract fun bindReminderBootReceiver(builder: ReminderBootReceiverSubComponent.Builder): AndroidInjector.Factory<*>

}