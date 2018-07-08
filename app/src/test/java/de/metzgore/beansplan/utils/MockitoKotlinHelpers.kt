package de.metzgore.beansplan.utils

import org.mockito.ArgumentCaptor

/**
 * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException
 * when null is returned.
 */
inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)