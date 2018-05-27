package de.metzgore.beansplan.data

import de.metzgore.beansplan.data.Status.*

data class Resource<out T>(val status: Status, val data: T?, val message:
String?, val forceRefresh: Boolean) {

    companion object {

        fun <T> success(data: T?, refreshForced: Boolean): Resource<T> {
            return Resource(SUCCESS, data, null, refreshForced)
        }

        fun <T> error(msg: String, data: T?, refreshForced: Boolean): Resource<T> {
            return Resource(ERROR, data, msg, refreshForced)
        }

        fun <T> loading(data: T?, refreshForced: Boolean): Resource<T> {
            return Resource(LOADING, data, null, refreshForced)
        }
    }
}