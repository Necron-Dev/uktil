@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.Ref
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal
import kotlinx.serialization.Serializable

@Serializable
data class Mut<T>(
    @property:UktilInternal @JvmField var content: T,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = content

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        content = value
    }
}

inline var <T> Mut<T>.value
    @OptIn(UktilInternal::class)
    get() = content

    @OptIn(UktilInternal::class)
    set(value) {
        content = value
    }

@OptIn(UktilInternal::class)
@Suppress("UNCHECKED_CAST")
inline fun <R> Mut<*>.cast() = content as R

inline val <T> T.inMut get() = Mut(this)

inline val <T> Out<T>.reMut get() = Mut(value)

inline fun <T> nullMut(): Mut<T?> = null.inMut
