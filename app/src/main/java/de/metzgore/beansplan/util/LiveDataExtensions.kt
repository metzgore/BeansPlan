package de.metzgore.beansplan.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    var lastValue: Any? = Any()
    return MediatorLiveData<T>().apply {
        addSource(this@distinctUntilChanged) {
            if (it != lastValue) {
                lastValue = it
                postValue(it)
            }
        }
    }
}