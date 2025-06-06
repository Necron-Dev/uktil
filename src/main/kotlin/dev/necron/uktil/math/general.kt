@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.math

inline fun lerp(
    from: Double,
    to: Double,
    progress: Double,
) = from + (to - from) * progress
