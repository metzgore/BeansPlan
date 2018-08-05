package de.metzgore.beansplan.util.di.components

import android.content.BroadcastReceiver
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.BroadcastReceiverKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.notifications.ReminderBootReceiver

@Module(subcomponents = [(ReminderBootReceiverSubComponent::class)])
abstract class ReminderBootReceiverComponent {

    @Binds
    @IntoMap
    @BroadcastReceiverKey(ReminderBootReceiver::class)
    abstract fun bindReminderBootReceiver(builder: ReminderBootReceiverSubComponent.Builder): AndroidInjector.Factory<out BroadcastReceiver>

}