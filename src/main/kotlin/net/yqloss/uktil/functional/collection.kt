@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.functional

inline operator fun <R, T : Function<R>> MutableCollection<T>.invoke(function: T) {
    this += function
}
