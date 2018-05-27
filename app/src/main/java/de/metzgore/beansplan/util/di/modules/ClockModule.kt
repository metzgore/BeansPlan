package de.metzgore.beansplan.util.di.modules

import dagger.Module
import dagger.Provides
import de.metzgore.beansplan.util.Clock
import de.metzgore.beansplan.util.ClockImpl

@Module
class ClockModule {

    @Provides
    fun clock(): Clock {
        return ClockImpl()
    }
}