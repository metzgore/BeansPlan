package de.metzgore.beansplan.util.di.components

import android.content.BroadcastReceiver
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.BroadcastReceiverKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.notifications.NotificationPublisher

@Module(subcomponents = [(NotificationPublisherSubComponent::class)])
abstract class NotificationPublisherComponent {

    @Binds
    @IntoMap
    @BroadcastReceiverKey(NotificationPublisher::class)
    abstract fun bindNotificationPublisher(builder: NotificationPublisherSubComponent.Builder): AndroidInjector.Factory<out BroadcastReceiver>

}