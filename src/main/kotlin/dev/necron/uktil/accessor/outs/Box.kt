@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.outs

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Box<out T>(
    @property:UktilInternal @JvmField val content: T,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = content
}

@OptIn(UktilInternal::class)
inline val <T> Box<T>.value get() = content

@OptIn(UktilInternal::class)
@Suppress("UNCHECKED_CAST")
inline fun <R> Box<*>.cast() = content as R

inline val <T> T.inBox get() = Box(this)

inline val <T> Out<T>.reBox get() = Box(value)

val nullBox: Box<Nothing?> = null.inBox

inline fun <T> nullBox(): Box<T?> = nullBox
