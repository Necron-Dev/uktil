@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.accessor

import kotlin.reflect.KProperty

interface Out<out T> : () -> T {
    fun get(): T

    override fun invoke() = get()
}

inline val <T> Out<T>.value get() = get()

@Suppress("UNCHECKED_CAST")
inline fun <R> Out<*>.cast() = get() as R

inline operator fun <X, T> Out<T>.getValue(
    thisRef: X,
    property: KProperty<*>,
) = value

inline fun <T> makeOut(crossinline getter: () -> T) = object : Out<T> {
    override fun get() = getter()
}

inline val <T> (() -> T).asOut get() = makeOut(this)

inline fun <T> nullOut(): Out<T> = makeOut { throw UnsupportedOperationException("get value from a null out") }
