@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.functional

inline operator fun <R, T : Function<R>> MutableCollection<T>.invoke(function: T) {
    this += function
}
