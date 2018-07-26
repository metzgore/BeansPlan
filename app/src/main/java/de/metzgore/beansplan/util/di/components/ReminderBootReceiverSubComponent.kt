package de.metzgore.beansplan.util.di.components

import dagger.Subcomponent
import dagger.android.AndroidInjector
import de.metzgore.beansplan.notifications.ReminderBootReceiver

@Subcomponent
interface ReminderBootReceiverSubComponent : AndroidInjector<ReminderBootReceiver> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ReminderBootReceiver>()
}