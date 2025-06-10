@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import kotlinx.serialization.Serializable
import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

@Serializable
data class Mut<T>(
    @property:UktilInternal @JvmField var internalContent: T,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = internalContent

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        internalContent = value
    }
}

inline var <T> Mut<T>.value
    @OptIn(UktilInternal::class)
    get() = internalContent

    @OptIn(UktilInternal::class)
    set(value) {
        internalContent = value
    }

@OptIn(UktilInternal::class)
@Suppress("UNCHECKED_CAST")
inline fun <R> Mut<*>.cast() = internalContent as R

inline val <T> T.inMut get() = Mut(this)

inline val <T> Out<T>.reMut get() = Mut(value)

inline fun <T> nullMut(): Mut<T?> = null.inMut
