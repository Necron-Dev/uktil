@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.annotation.UktilInternal

data class ThreadLocalRef<T>(
    @property:UktilInternal @JvmField val internalHolder: ThreadLocal<T>,
) : Ref<T> {
    constructor(initializer: () -> T) : this(ThreadLocal.withInitial(initializer))

    @OptIn(UktilInternal::class)
    override inline fun get(): T = internalHolder.get()

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) = internalHolder.set(value)
}

@OptIn(UktilInternal::class)
inline fun <T> threadLocal(noinline initializer: () -> T) = ThreadLocalRef(initializer)
