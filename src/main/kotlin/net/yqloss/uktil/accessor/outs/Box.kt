@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.outs

import kotlinx.serialization.Serializable
import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

@JvmInline
@Serializable
value class Box<out T>(
    @property:UktilInternal @JvmField val internalContent: T,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = internalContent
}

@OptIn(UktilInternal::class)
inline val <T> Box<T>.value get() = internalContent

@OptIn(UktilInternal::class)
@Suppress("UNCHECKED_CAST")
inline fun <R> Box<*>.cast() = internalContent as R

inline val <T> T.inBox get() = Box(this)

inline val <T> Out<T>.reBox get() = Box(value)

val nullBox: Box<Nothing?> = null.inBox

inline fun <T> nullBox(): Box<T?> = nullBox
