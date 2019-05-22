package de.metzgore.beansplan.util.di.components

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import de.metzgore.beansplan.notifications.NotificationPublisher

@Module(subcomponents = [(NotificationPublisherSubComponent::class)])
abstract class NotificationPublisherComponent {

    @Binds
    @IntoMap
    @ClassKey(NotificationPublisher::class)
    abstract fun bindNotificationPublisher(builder: NotificationPublisherSubComponent.Builder): AndroidInjector.Factory<*>

}