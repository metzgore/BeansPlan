package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.notifications.NotificationPublisher

@Subcomponent
interface NotificationPublisherSubComponent : AndroidInjector<NotificationPublisher> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NotificationPublisher>()
}