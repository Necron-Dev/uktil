@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.accessor

import kotlin.reflect.KProperty

interface In<in T> : (T) -> @UnsafeVariance T {
    fun set(value: T)

    override fun invoke(value: T): @UnsafeVariance T = value.also(::set)
}

inline operator fun <X, T> In<T>.setValue(
    thisRef: X,
    property: KProperty<*>,
    value: T,
) = set(value)

inline fun <T> makeIn(crossinline setter: (T) -> Unit) = object : In<T> {
    override fun set(value: T) = setter(value)
}

inline val <T> ((T) -> Unit).asIn get() = makeIn(this)

inline fun <T> nullIn(): In<T> = makeIn { throw UnsupportedOperationException("set value into a null in") }
