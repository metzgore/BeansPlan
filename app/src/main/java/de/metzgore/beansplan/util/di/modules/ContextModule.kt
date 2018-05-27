package de.metzgore.beansplan.util.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun context(): Context {
        return context.applicationContext
    }
}
